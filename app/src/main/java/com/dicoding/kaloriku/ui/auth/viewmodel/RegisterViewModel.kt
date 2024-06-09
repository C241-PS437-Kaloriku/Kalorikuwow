package com.dicoding.kaloriku.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.kaloriku.data.RegisterRequest
import com.dicoding.kaloriku.data.RegisterResponse
import com.dicoding.kaloriku.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class RegisterViewModel : ViewModel() {
    private val _registerResult = MutableLiveData<RegisterResponse?>()
    val registerResult: LiveData<RegisterResponse?> = _registerResult

    fun register(email: String, password: String, birthdate: Date) {
        val registerRequest = RegisterRequest(email, password, birthdate) // Create a RegisterRequest instance
        val client = ApiConfig.getApiService().register(registerRequest)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    _registerResult.value = response.body()
                    Log.d("RegisterViewModel", "Registration successful: $response")
                } else {
                    _registerResult.value = null
                    Log.e("RegisterViewModel", "Registration failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _registerResult.value = null
                Log.e("RegisterViewModel", "Registration failed due to: ${t.message}", t)
            }
        })
    }
}
