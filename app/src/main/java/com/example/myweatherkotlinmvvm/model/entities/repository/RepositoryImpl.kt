package com.example.myweatherkotlinmvvm.model.entities.repository

import com.example.myweatherkotlinmvvm.model.entities.Weather
import com.example.myweatherkotlinmvvm.model.entities.getRussianCities
import com.example.myweatherkotlinmvvm.model.entities.getWorldCities

class RepositoryImpl : Repository {
    override fun getWeatherFromServer() = Weather()
    override fun getWeatherFromLocalStorageRus() = getRussianCities()
    override fun getWeatherFromLocalStorageWorld() = getWorldCities()
}