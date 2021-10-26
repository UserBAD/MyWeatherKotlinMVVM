package com.example.myweatherkotlinmvvm.framework.ui.details

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myweatherkotlinmvvm.AppState
import com.example.myweatherkotlinmvvm.model.entities.repository.Repository

class DetailsViewModel(private val repository: Repository) : ViewModel(), LifecycleObserver {
    val liveDataToObserve: MutableLiveData<AppState> = MutableLiveData()

    fun loadData(lat: Double, lon: Double) {
        liveDataToObserve.value = AppState.Loading
        Thread {
            val data = repository.getWeatherFromServer(lat, lon)
            liveDataToObserve.postValue(AppState.Success(listOf(data)))
        }.start()
    }
}