package com.example.myweatherkotlinmvvm.framework.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myweatherkotlinmvvm.AppState
import com.example.myweatherkotlinmvvm.R
import com.example.myweatherkotlinmvvm.databinding.MainFragmentBinding
import com.example.myweatherkotlinmvvm.di.showSnackBar
import com.example.myweatherkotlinmvvm.framework.ui.adapters.MainFragmentAdapter
import com.example.myweatherkotlinmvvm.framework.ui.details.DetailsFragment
import com.example.myweatherkotlinmvvm.model.entities.Weather
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {
    private val viewModel: MainViewModel by viewModel()

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private var adapter: MainFragmentAdapter? = null
    private var isDataSetRus: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            mainFragmentRecyclerView.adapter = adapter
            mainFragmentFAB.setOnClickListener { changeWeatherDataSet() }
            viewModel.getLiveData().observe(viewLifecycleOwner, { renderData(it) })
            viewModel.getWeatherFromLocalStorageRus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeWeatherDataSet() = with(binding) {
        if (isDataSetRus) {
            viewModel.getWeatherFromLocalSourceWorld()
            mainFragmentFAB.setImageResource(R.drawable.ic_earth)
        } else {
            viewModel.getWeatherFromLocalStorageRus()
            mainFragmentFAB.setImageResource(R.drawable.ic_russia)
        }
        isDataSetRus = !isDataSetRus
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