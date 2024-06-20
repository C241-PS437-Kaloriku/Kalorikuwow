package com.dicoding.kaloriku.ui.auth.viewmodel

import android.provider.Settings
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.response.FoodItem
import com.dicoding.kaloriku.data.response.FoodRecommendationRequest
import com.dicoding.kaloriku.data.response.FoodRecommendationResponse
import com.dicoding.kaloriku.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProgressViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _dailyCaloriesNeeded = MutableLiveData<Double>()
    val dailyCaloriesNeeded: LiveData<Double> = _dailyCaloriesNeeded

    private val _eatenCalories = MutableLiveData<Double>()
    val eatenCalories: LiveData<Double> = _eatenCalories

    private val _eatenCarbs = MutableLiveData<Double>()
    val eatenCarbs: LiveData<Double> = _eatenCarbs

    private val _eatenProteins = MutableLiveData<Double>()
    val eatenProteins: LiveData<Double> = _eatenProteins

    private val _eatenFats = MutableLiveData<Double>()
    val eatenFats: LiveData<Double> = _eatenFats

    init {
        // Initialize with default values
        _dailyCaloriesNeeded.value = 0.0
        _eatenCalories.value = 0.0
        _eatenCarbs.value = 0.0
        _eatenProteins.value = 0.0
        _eatenFats.value = 0.0
    }

    fun setDailyCaloriesNeeded(calories: Double) {
        _dailyCaloriesNeeded.value = calories
    }

    fun addEatenFood(calories: Double, carbs: Double, proteins: Double, fats: Double) {
        _eatenCalories.value = (_eatenCalories.value ?: 0.0) + calories
        _eatenCarbs.value = (_eatenCarbs.value ?: 0.0) + carbs
        _eatenProteins.value = (_eatenProteins.value ?: 0.0) + proteins
        _eatenFats.value = (_eatenFats.value ?: 0.0) + fats
    }

    fun getDailyCalories(
        weight: Int,
        height: Int,
        age: Int,
        goal: String,
        callback: (Float) -> Unit
    ) {
        val request = FoodRecommendationRequest(weight, height, age, goal) // Replace with your actual request object
        val apiService =  ApiConfig.getPredictService()

        apiService.getFoodRecommendations(request).enqueue(object : Callback<FoodRecommendationResponse> {
            override fun onResponse(
                call: Call<FoodRecommendationResponse>,
                response: Response<FoodRecommendationResponse>
            ) {
                if (response.isSuccessful) {
                    val dailyCaloriesNeeded = response.body()?.daily_calories_needed ?: 0f
                    callback(dailyCaloriesNeeded)
                } else {
                    Log.e("ProfileFragment", "API call failed with response code: ${response.code()}")
                    callback(0f)
                }
            }

            override fun onFailure(call: Call<FoodRecommendationResponse>, t: Throwable) {
                Log.e("ProfileFragment", "API call failed: ${t.message}")
                callback(0f)
            }
        })
    }
    }


