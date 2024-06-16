package com.dicoding.kaloriku.ui.auth.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.pref.UserModel
import com.dicoding.kaloriku.data.response.LoginRequest
import com.dicoding.kaloriku.data.response.LoginResponse
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.response.ProfileResponse
import com.dicoding.kaloriku.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    fun login(email: String, password: String) {
        val loginRequest = LoginRequest(email, password)
        val client = ApiConfig.getApiService().login(loginRequest)
        client.enqueue(object : retrofit2.Callback<LoginResponse> {
            override fun onResponse(call: retrofit2.Call<LoginResponse>, response: retrofit2.Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    _loginResult.value = loginResponse

                    loginResponse?.let {
                        val token = it.token ?: ""
                        val userId = it.user?.userId ?: ""
                        Log.d("LoginViewModel", "Token received from server: $token")
                        Log.d("LoginViewModel", "UserId received from server: $userId")
                        val user = UserModel(email = it.user?.email ?: "", token = token, isLogin = true, userId = userId)
                        saveSession(user)
                        saveToken(token)
                    }
                } else {
                    _loginResult.value = null
                    Log.e("LoginViewModel", "Login failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<LoginResponse>, t: Throwable) {
                _loginResult.value = null
                Log.e("LoginViewModel", "Login failed due to: ${t.message}", t)
            }
        })
    }

    fun getUserProfile(token: String, userId: String): LiveData<ProfileResponse?> {
        val userProfile = MutableLiveData<ProfileResponse?>()
        viewModelScope.launch {
            try {
                val response = repository.getPhysicalData(token, userId)
                if (response.isSuccessful) {
                    userProfile.postValue(response.body())
                } else {
                    userProfile.postValue(null)
                }
            } catch (e: Exception) {
                userProfile.postValue(null)
            }
        }
        return userProfile
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    private fun saveToken(token: String) {
        viewModelScope.launch {
            repository.saveToken(token)
        }
    }
}