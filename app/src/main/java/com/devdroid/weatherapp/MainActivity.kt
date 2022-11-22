package com.devdroid.weatherapp

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devdroid.weatherapp.Models.WeatherModel
import com.devdroid.weatherapp.service.WeatherApiService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {


    val key: String = "695c1cd3a43b5c8ebcc0703c47b4e919"
    lateinit var lat: String
    lateinit var long: String
    // lateinit var call: WeatherModel

    lateinit var edtCityName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lat = intent.getStringExtra("lat").toString()
        long = intent.getStringExtra("long").toString()

        edtCityName = findViewById(R.id.edtCityName)

        CoroutineScope(Dispatchers.Main).launch {

            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE

            var call = doInBackground()

            if(call is WeatherModel){
                setData(call)

                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
            }else {
                Toast.makeText(
                    this@MainActivity,
                    call as String?,
                    Toast.LENGTH_LONG
                ).show()
            }



        }

        CoroutineScope(Dispatchers.Main).launch { }

        searchCityWeather()


        /*
        GlobalScope.launch(Dispatchers.Main) {
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
            val result = doInBackground().toString()
            val jsonObj = JSONObject(result)
            val main = jsonObj.getJSONObject("main")
            val sys = jsonObj.getJSONObject("sys")
            val wind = jsonObj.getJSONObject("wind")
            val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
            val updatedAt: Long = jsonObj.getLong("dt")
            val updatedAtText =
                "Updated At: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updatedAt * 1000)
                )
            val temp = main.getString("temp") + "℃"
            val tempMin = "Min: " + main.getString("temp_min") + "℃"
            val tempMax = "Max: " + main.getString("temp_max") + "℃"
            val pressure = main.getString("pressure")
            val humidity = main.getString("humidity") + "%"
            val sunrise: Long = sys.getLong("sunrise")
            val sunset: Long = sys.getLong("sunset")
            val windSpeed = wind.getString("speed") + "Km/h"
            val weatherDescription = weather.getString("description")
            val address = jsonObj.getString("name") + ", " + sys.getString("country")

            findViewById<TextView>(R.id.address).text = address
            findViewById<TextView>(R.id.updatedAt).text = updatedAtText
            findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
            findViewById<TextView>(R.id.temperature).text = temp
            findViewById<TextView>(R.id.tempMin).text = tempMin
            findViewById<TextView>(R.id.tempMax).text = tempMax
            findViewById<TextView>(R.id.sunrise).text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
            findViewById<TextView>(R.id.sunset).text =
                SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
            findViewById<TextView>(R.id.wind).text = windSpeed
            findViewById<TextView>(R.id.pressure).text = pressure
            findViewById<TextView>(R.id.humidity).text = humidity

            findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
        }*/


    }

    private fun setData(call: WeatherModel) {
        val updatedAtText =
            "Updated At: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                Date(call.dt * 1000)
            )

        val temp = call.main.temp.toString() + "℃"
        val tempMin = "Min: " + call.main.tempMin + "℃"
        val tempMax = "Max: " + call.main.tempMax + "℃"
        val pressure = call.main.pressure.toString()
        val humidity = call.main.humidity.toString() + "%"
        val sunrise: Long = call.sys.sunrise
        val sunset: Long = call.sys.sunset
        val windSpeed = call.wind.speed.toString() + "Km/h"
        val weatherDescription = call.weather
        val address = call.name + ", " + call.sys.country

        findViewById<TextView>(R.id.address).text = address
        findViewById<TextView>(R.id.updatedAt).text = updatedAtText
        findViewById<TextView>(R.id.status).text =
            weatherDescription.get(0).description.capitalize()
        findViewById<TextView>(R.id.temperature).text = temp
        findViewById<TextView>(R.id.tempMin).text = tempMin
        findViewById<TextView>(R.id.tempMax).text = tempMax
        findViewById<TextView>(R.id.sunrise).text =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
        findViewById<TextView>(R.id.sunset).text =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
        findViewById<TextView>(R.id.wind).text = windSpeed
        findViewById<TextView>(R.id.pressure).text = pressure
        findViewById<TextView>(R.id.humidity).text = humidity


    }


    private suspend fun doInBackground(): Any? = withContext(Dispatchers.IO) {
        try {
            return@withContext WeatherApiService.api.getData(lat, long, key)
        }catch(e:Exception){
            return@withContext e.message
        }
    }

    private fun searchCityWeather() {
        edtCityName.setOnKeyListener { v, keyCode, event ->

            when {
                ((keyCode == KeyEvent.KEYCODE_ENTER) && (event.action == KeyEvent.ACTION_DOWN)) -> {

                    val city = edtCityName.text.toString()
                    CoroutineScope(Dispatchers.Main).launch {
                        if (city.isNotEmpty()) {
                            var call = doInBackground2(city)
                            if (call is WeatherModel) {
                                setData(call)
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    call as String?,
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }else{
                            Toast.makeText(this@MainActivity,"Enter a city",Toast.LENGTH_SHORT).show()
                        }
                    }
                    return@setOnKeyListener true
                }
                else -> false
            }


        }
    }

    private suspend fun doInBackground2(city: String): Any? = withContext(Dispatchers.IO) {
        try {
            return@withContext WeatherApiService.api.getData2(city, key)
        } catch (e: Exception) {
            return@withContext e.message
        }

    }


}