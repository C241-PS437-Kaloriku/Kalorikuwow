package com.dicoding.kaloriku.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.LoginRequest
import com.dicoding.kaloriku.data.pref.UserModel
import com.dicoding.kaloriku.data.LoginResponse
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password) // Create a LoginRequest instance
        val client = ApiConfig.getApiService().login(loginRequest)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    _loginResult.value = response.body()
                    response.body()?.user?.password?.let { saveTokenToDataStore(it) } // Save token to DataStore
                    Log.d("LoginViewModel", "Login successful: $response")
                } else {
                    _loginResult.value = null
                    Log.e("LoginViewModel", "Login failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResult.value = null
                Log.e("LoginViewModel", "Login failed due to: ${t.message}", t)
            }
        })
    }

    private fun saveTokenToDataStore(token: String) {
        viewModelScope.launch {
            repository.saveToken(token)
        }
    }
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}