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
    fun getWeatherFromLocalStorageRus() = getDataFromLocalSource(isRussian = true)
    fun getWeatherFromLocalSourceWorld()= getDataFromLocalSource(false)
    private fun getDataFromLocalSource(isRussian: Boolean) {
        liveData.value = AppState.Loading
        Thread {
            sleep(1000)
            liveData.postValue(
                if (isRussian) {
                    AppState.Success(repository.getWeatherFromLocalStorageRus())
                } else{
                    AppState.Success(repository.getWeatherFromLocalStorageWorld())
                }
            )
        }.start()
    }
}