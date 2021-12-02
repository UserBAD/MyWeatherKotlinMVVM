package com.example.myweatherkotlinmvvm.framework.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myweatherkotlinmvvm.AppState
import com.example.myweatherkotlinmvvm.R
import com.example.myweatherkotlinmvvm.databinding.MainFragmentBinding
import com.example.myweatherkotlinmvvm.di.showSnackBar
import com.example.myweatherkotlinmvvm.framework.ui.adapters.MainFragmentAdapter
import com.example.myweatherkotlinmvvm.framework.ui.details.DetailsFragment
import com.example.myweatherkotlinmvvm.model.entities.City
import com.example.myweatherkotlinmvvm.model.entities.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.IOException
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit


private const val dataSetKey = "dataSetKey"

class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModel()

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private var adapter: MainFragmentAdapter? = null
    private var isDataSetRus: Boolean = true

    private val permissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                getLocation()
            } else {
                Toast.makeText(
                    context,
                    getString(R.string.dialog_message_no_gps),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val onLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            getAddressAsync(location)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            mainFragmentRecyclerView.adapter = adapter
            mainFragmentFAB.setOnClickListener { changeWeatherDataSet() }
            mainFragmentFABLocation.setOnClickListener { checkPermission() }
            viewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
            viewModel.getWeatherFromLocalStorageRus()
        }

        loadDataSet()
        initDataSet()


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeWeatherDataSet() = with(binding) {
        isDataSetRus = !isDataSetRus
        initDataSet()
    }

    private fun loadDataSet() {
        activity?.let {
            isDataSetRus = activity
                ?.getPreferences(Context.MODE_PRIVATE)
                ?.getBoolean(dataSetKey, true) ?: true
        }
    }

    private fun initDataSet() = with(binding) {
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceWorld()
            mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getWeatherFromLocalStorageRus()
            mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }
        saveDataSetToDisk()
    }

    private fun saveDataSetToDisk() {
        val editor = activity?.getPreferences(Context.MODE_PRIVATE)?.edit()
        editor?.putBoolean(dataSetKey, isDataSetRus)
        editor?.apply()
    }

    private fun checkPermission() {
        context?.let { notNullContext ->
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    notNullContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {
                    getLocation()
                }
                else -> {
                    permissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        activity?.let { context ->
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val provider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                provider?.let {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        10f,
                        onLocationListener
                    )
                }
            } else {
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location == null) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.looks_like_location_disabled),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    getAddressAsync(location)
                }
            }
        }
    }

    private fun getAddressAsync(location: Location) = with(binding) {
        val geoCoder = Geocoder(context)
        launch(Dispatchers.IO) {
            try {
                val addresses = geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                withContext(Dispatchers.Main) {
                    showAddressDialog(addresses[0].getAddressLine(0), location)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_address_title))
                .setMessage(address)
                .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ ->
                    openDetailsFragment(
                        Weather(
                            City(
                                address,
                                location.latitude,
                                location.longitude
                            )
                        )
                    )
                }
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun openDetailsFragment(weather: Weather) {
        activity?.supportFragmentManager?.let { manager ->
            val bundle = Bundle().apply {
                putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
            }
            manager.beginTransaction()
                .add(R.id.container, DetailsFragment.newInstance(bundle))
                .addToBackStack("")
                .commitAllowingStateLoss()
        }
    }

    private fun renderData(appState: AppState) = with(binding) {
        when (appState) {
            is AppState.Success -> {
                mainFragmentLoadingLayout.visibility = View.GONE
                adapter = MainFragmentAdapter(object : OnItemViewClickListener {
                    override fun onItemViewClick(weather: Weather) {
                        val manager = activity?.supportFragmentManager
                        manager?.let { manager ->
                            val bundle = Bundle().apply {
                                putParcelable(DetailsFragment.BUNDLE_EXTRA, weather)
                            }
                            manager.beginTransaction()
                                .add(R.id.container, DetailsFragment.newInstance(bundle))
                                .addToBackStack("")
                                .commitAllowingStateLoss()
                        }
                    }
                }).apply {
                    setWeather(appState.weatherData)
                }
                mainFragmentRecyclerView.adapter = adapter
            }
            is AppState.Loading -> {
                mainFragmentLoadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                mainFragmentLoadingLayout.visibility = View.GONE

                mainFragmentFAB.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload)
                ) {
                    viewModel.getWeatherFromLocalStorageRus()
                }
            }
        }
    }


    interface OnItemViewClickListener {
        fun onItemViewClick(weather: Weather)

    }

    companion object {
        fun newInstance() = MainFragment()
    }
}

private fun MainFragmentBinding.launch(
    context: CoroutineDispatcher,
    block: suspend CoroutineScope.() -> Unit
) {

}
