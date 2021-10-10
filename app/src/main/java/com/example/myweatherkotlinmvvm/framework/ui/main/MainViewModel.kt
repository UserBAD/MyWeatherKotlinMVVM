package com.example.myweatherkotlinmvvm.framework.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myweatherkotlinmvvm.AppState
import com.example.myweatherkotlinmvvm.model.entities.repository.Repository
import java.lang.Thread.sleep

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val liveData = MutableLiveData<AppState>()
    fun getLiveData(): LiveData<AppState> = liveData
    fun getWeather() = getDataFromLocalSource()
    private fun getDataFromLocalSource() {
        liveData.value = AppState.Loading
        Thread {
            sleep(1000)
            liveData.postValue(AppState.Success(repository.getWeatherFromLocalStorage()))
        }.start()
    }
}