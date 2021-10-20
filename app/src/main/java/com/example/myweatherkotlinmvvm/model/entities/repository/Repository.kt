package com.example.myweatherkotlinmvvm.model.entities.repository

import com.example.myweatherkotlinmvvm.model.entities.Weather

interface Repository {
    fun getWeatherFromServer(lat: Double, lng: Double): Weather
    fun getWeatherFromLocalStorageRus(): List<Weather>
    fun getWeatherFromLocalStorageWorld(): List<Weather>
}