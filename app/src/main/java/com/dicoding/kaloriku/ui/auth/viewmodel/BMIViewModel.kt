package com.dicoding.kaloriku.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.response.BMIResponse
import com.dicoding.kaloriku.data.retrofit.ApiConfig
import kotlinx.coroutines.launch



class BMIViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val apiService = ApiConfig.getApiService()

    private val _bmiResult = MutableLiveData<BMIResponse>()
    val bmiResult: LiveData<BMIResponse> = _bmiResult

    fun calculateBMI(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.getToken().collect { token ->
                    val response = apiService.calculateBMI(token, userId)
                    println(token)
                    if (response.isSuccessful) {
                        val bmiResponse = response.body()
                        bmiResponse?.let {
                            _bmiResult.value = it
                            Log.d("BMIViewModel", "BMI: ${it.bmi}, Category: ${it.category}")
                        }
                    } else {
                        Log.e("BMIViewModel", "Response error: ${response.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("BMIViewModel", "Error fetching BMI data", e)
            }
        }
    }
}

