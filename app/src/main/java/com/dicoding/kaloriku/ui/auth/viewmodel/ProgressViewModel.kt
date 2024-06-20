package com.dicoding.kaloriku.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.dicoding.kaloriku.data.dao.DailyConsumptionDao
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.response.DailyConsumption
import com.dicoding.kaloriku.data.response.FoodRecommendationRequest
import com.dicoding.kaloriku.data.response.FoodRecommendationResponse
import com.dicoding.kaloriku.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ProgressViewModel(
    private val userRepository: UserRepository,
    private val dailyConsumptionDao: DailyConsumptionDao
) : ViewModel() {

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> = _selectedDate

    private val _dailyConsumption = _selectedDate.switchMap { date ->
        dailyConsumptionDao.getConsumptionForDate(date).asLiveData()
    }
    private val _dailyCaloriesNeeded = MutableLiveData(0.0)
    val dailyCaloriesNeeded: LiveData<Double> = _dailyCaloriesNeeded

    val eatenCalories: LiveData<Double> = _dailyConsumption.map { it?.calories ?: 0.0 }
    val eatenCarbs: LiveData<Double> = _dailyConsumption.map { it?.carbs ?: 0.0 }
    val eatenProteins: LiveData<Double> = _dailyConsumption.map { it?.proteins ?: 0.0 }
    val eatenFats: LiveData<Double> = _dailyConsumption.map { it?.fats ?: 0.0 }

    val remainingCalories = MediatorLiveData<Double>().apply {
        addSource(_dailyCaloriesNeeded) { calculateRemainingCalories() }
        addSource(eatenCalories) { calculateRemainingCalories() }
    }

    private fun calculateRemainingCalories() {
        val daily = _dailyCaloriesNeeded.value ?: 0.0
        val eaten = eatenCalories.value ?: 0.0
        remainingCalories.value = daily - eaten
    }

    fun setDate(date: Date) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _selectedDate.value = formatter.format(date)
    }

    fun addEatenFood(calories: Double, carbs: Double, proteins: Double, fats: Double) {
        viewModelScope.launch {
            val currentDate = _selectedDate.value ?: return@launch
            val currentConsumption = _dailyConsumption.value ?: DailyConsumption(currentDate, 0.0, 0.0, 0.0, 0.0)
            val updatedConsumption = currentConsumption.copy(
                calories = currentConsumption.calories + calories,
                carbs = currentConsumption.carbs + carbs,
                proteins = currentConsumption.proteins + proteins,
                fats = currentConsumption.fats + fats
            )
            dailyConsumptionDao.insertOrUpdate(updatedConsumption)
        }
    }

    fun setDailyCaloriesNeeded(calories: Double) {
        _dailyCaloriesNeeded.value = calories
    }

    fun getDailyCalories(weight: Int, height: Int, age: Int, goal: String, callback: (Float) -> Unit) {
        val request = FoodRecommendationRequest(weight, height, age, goal)
        val apiService = ApiConfig.getPredictService()

        apiService.getFoodRecommendations(request).enqueue(object : Callback<FoodRecommendationResponse> {
            override fun onResponse(
                call: Call<FoodRecommendationResponse>,
                response: Response<FoodRecommendationResponse>
            ) {
                if (response.isSuccessful) {
                    val dailyCaloriesNeeded = response.body()?.daily_calories_needed ?: 0f
                    setDailyCaloriesNeeded(dailyCaloriesNeeded.toDouble())
                    callback(dailyCaloriesNeeded)
                } else {
                    Log.e("ProgressViewModel", "API call failed with response code: ${response.code()}")
                    callback(0f)
                }
            }

            override fun onFailure(call: Call<FoodRecommendationResponse>, t: Throwable) {
                Log.e("ProgressViewModel", "API call failed: ${t.message}")
                callback(0f)
            }
        })
    }
}