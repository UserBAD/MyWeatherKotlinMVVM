package com.example.myweatherkotlinmvvm.framework.ui.details

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweatherkotlinmvvm.AppState
import com.example.myweatherkotlinmvvm.model.entities.City
import com.example.myweatherkotlinmvvm.model.entities.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailsViewModel(private val repository: Repository) : ViewModel(), LifecycleObserver {
    val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData()

    fun loadData(city: City) {
        liveDataToObserve.value = AppState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getWeatherFromServer(city.lat, city.lon)
            data.city = city
            repository.saveEntity(data)
            liveDataToObserve.postValue(AppState.Success(listOf(data)))
        }
    }
}