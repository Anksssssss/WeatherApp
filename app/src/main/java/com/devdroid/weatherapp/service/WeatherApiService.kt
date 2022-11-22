package com.devdroid.weatherapp.service

import com.devdroid.weatherapp.Models.WeatherModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherApiService {

    //https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}
    private val BASE_URL = "https://api.openweathermap.org/"

    val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WeatherApi::class.java)

}