package com.example.myweatherkotlinmvvm.framework.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import coil.load
import coil.transform.GrayscaleTransformation
import com.example.myweatherkotlinmvvm.AppState
import com.example.myweatherkotlinmvvm.R
import com.example.myweatherkotlinmvvm.databinding.DetailsFragmentBinding
import com.example.myweatherkotlinmvvm.framework.ui.main.MainViewModel
import com.example.myweatherkotlinmvvm.model.entities.Weather
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class DetailsFragment : Fragment() {

    companion object {
        const val BUNDLE_EXTRA = "weather"
        fun newInstance(bundle: Bundle): DetailsFragment {
            val fragment = DetailsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val viewModel: DetailsViewModel by viewModel()
    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let {
            with(binding) {
                cityName.text = it.city.city
                cityCoordinates.text = String.format(
                    getString(R.string.city_coordinates),
                    it.city.lat.toString(),
                    it.city.lon.toString()
                )
                viewModel.liveDataToObserve.observe(viewLifecycleOwner, { appState ->
                    when (appState) {
                        is AppState.Error -> {
                            mainView.visibility = View.INVISIBLE
                            loadingLayout.visibility = View.GONE
                            errorTV.visibility = View.VISIBLE
                        }
                        AppState.Loading -> {
                            mainView.visibility = View.INVISIBLE
                            binding.loadingLayout.visibility = View.VISIBLE
                        }
                        is AppState.Success -> {
                            loadingLayout.visibility = View.GONE
                            mainView.visibility = View.VISIBLE
                            temperatureValue.text = appState.weatherData[0].temperature.toString()
                            feelsLikeValue.text = appState.weatherData[0].feelsLike.toString()
                            weatherCondition.text = appState.weatherData[0].condition
                        }
                    }
                })
                viewModel.loadData(it.city)
            }
        }
        binding.imageView.load("https://freepngimg.com/thumb/city/36275-3-city-hd.png") {
            crossfade(true)
            transformations(GrayscaleTransformation())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}