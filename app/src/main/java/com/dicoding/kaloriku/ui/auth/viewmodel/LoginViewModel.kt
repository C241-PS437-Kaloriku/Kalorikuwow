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
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
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

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _loginResult.value = null
                Log.e("LoginViewModel", "Login failed due to: ${t.message}", t)
            }
        })
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

    fun hasPhysicalData(): LiveData<Boolean> {
        val hasData = MutableLiveData<Boolean>()
        viewModelScope.launch {
            hasData.value = repository.hasPhysicalData()
        }
        return hasData
    }
}
