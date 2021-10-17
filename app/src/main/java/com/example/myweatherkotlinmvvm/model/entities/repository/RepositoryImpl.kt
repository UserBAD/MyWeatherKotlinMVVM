package com.example.myweatherkotlinmvvm.model.entities.repository

import com.example.myweatherkotlinmvvm.model.entities.City
import com.example.myweatherkotlinmvvm.model.entities.Weather


class RepositoryImpl : Repository {
    override fun getWeatherFromServer() = Weather()
    override fun getWeatherFromLocalStorageRus() = City.getRussianCities()
    override fun getWeatherFromLocalStorageWorld() = City.getWorldCities()
}