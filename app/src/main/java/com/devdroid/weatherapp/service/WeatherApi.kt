package com.devdroid.weatherapp.service

import com.devdroid.weatherapp.Models.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {

    //https://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}

    @GET("data/2.5/weather?")
    suspend fun getData(
        @Query("lat") lat: String,
        @Query("lon") long: String,
        @Query("appid") key: String,
        @Query("units") unit: String = "metric"
    ): WeatherModel

    //https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}

    @GET("data/2.5/weather?")
    suspend fun getData2(
        @Query("q") cityNAme : String,
        @Query("appid") key : String,
        @Query("units") unit: String = "metric"
    ):WeatherModel

}