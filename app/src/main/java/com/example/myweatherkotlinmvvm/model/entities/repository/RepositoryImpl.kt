package com.example.myweatherkotlinmvvm.model.entities.repository

import com.example.myweatherkotlinmvvm.model.WeatherLoader
import com.example.myweatherkotlinmvvm.model.entities.City
import com.example.myweatherkotlinmvvm.model.entities.Weather


class RepositoryImpl : Repository {
    override fun getWeatherFromServer(lat: Double, lng: Double): Weather {
        val dto = WeatherLoader.loadWeather(lat, lng)
        return Weather(
            temperature = dto?.fact?.temp ?: 0,
            feelsLike = dto?.fact?.feels_like ?: 0,
            condition = dto?.fact?.condition
        )
    }

    override fun getWeatherFromLocalStorageRus() = City.getRussianCities()
    override fun getWeatherFromLocalStorageWorld() = City.getWorldCities()
}