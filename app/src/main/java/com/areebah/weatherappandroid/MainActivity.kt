package com.areebah.weatherappandroid

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.areebah.weatherappandroid.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)

    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        //4497cda261d90da3dc44ce561f86b871


//        https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}

//        https://api.openweathermap.org/data/2.5/weather?q=toronto&appid=4497cda261d90da3dc44ce561f86b871



        fetchingWeatherData("Toronto")
        searchCitybyName()




    }



    private fun fetchingWeatherData(cityName:String){
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName,"4497cda261d90da3dc44ce561f86b871","metric" )
        response.enqueue(object:Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {

                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {

                    val temp = responseBody.main.temp.toString().toDouble()
                    val temperature= String.format("%.2f",temp)
                    val maxtemperature =String.format("%.2f", responseBody.main.temp_max.toString().toDouble())
                    val mintemperature =String.format("%.2f", responseBody.main.temp_min.toString().toDouble())
                    val humidity = responseBody.main.humidity
                    val wind = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure

                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"




                    binding.textViewTemp.text = "$temperature °C"
                    binding.textViewMax.text = "Max: $maxtemperature °C"
                    binding.textViewMin.text = "Min: $mintemperature °C"
                    binding.textViewHumidity.text = "$humidity %"
                    binding.textViewWind.text = "$wind m/s"
                    binding.textViewSunrise.text = "${time(sunRise)}"
                    binding.textViewSunset.text = "${time(sunSet)}"
                    binding.textViewSea.text = "$seaLevel hPa"
                    binding.textViewWeather.text = "$condition"
                    binding.textViewSunny.text = condition
                    binding.textViewDay.text= day(System.currentTimeMillis())
                    binding.textViewDate.text=date()
                    binding.textViewCityName.text="$cityName"



                    backgroundImageSetter(condition)

                } else {
                    Log.e(TAG, "Response was successful but no body found.")
                }
            }
            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }
        })




    }


    private fun time(timeStamp: Long):String {

        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        return simpleDateFormat.format(Date(timeStamp * 1000))

    }

    private fun backgroundImageSetter(condition:String) {

      when(condition)  {

          "Clear Sky", "Sunny", "Clear"->{
              binding.root.setBackgroundResource(R.drawable.sunny55)
              binding.lottieAnimationView.setAnimation(R.raw.sun)

          }


            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.raining55)
                binding.lottieAnimationView.setAnimation(R.raw.rain)

            }

          "Haze", "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
              binding.root.setBackgroundResource(R.drawable.haze66)
              binding.lottieAnimationView.setAnimation(R.raw.cloud)
          }

          "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow22)
                binding.lottieAnimationView.setAnimation(R.raw.snow)

            }

          else -> {
                binding.root.setBackgroundResource(R.drawable.sunny55)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()


    }

    fun day(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
//        return sdf.format((Date()))
        return sdf.format(Date(timestamp * 1000))
    }


    fun date():String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun searchCitybyName(){

        val searchView= binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchingWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               return true
            }
        })


    }



}

