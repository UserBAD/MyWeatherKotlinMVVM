package com.example.myweatherkotlinmvvm.model.rest_entities

import com.google.gson.annotations.SerializedName

data class FactDTO(
    val temp: Int?,
    @SerializedName("feels_like")
    val feelslike: Int?,

    val condition: String?
)

