package com.dicoding.kaloriku.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.data.response.UpdatePhysicalResponse
import com.dicoding.kaloriku.data.retrofit.ApiConfig
import com.dicoding.kaloriku.data.pref.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhysicalDataViewModel(private val repository: UserRepository) : ViewModel() {

    private val _updatePhysicalResult = MutableLiveData<Result<UpdatePhysicalResponse>>()
    val updatePhysicalResult: LiveData<Result<UpdatePhysicalResponse>> = _updatePhysicalResult

    fun getTokenAndUpdatePhysicalData(request: UpdatePhysicalRequest) {
        viewModelScope.launch {
            val token = repository.getToken().first()
            val userId = repository.getUserId().first() // Pastikan ada metode ini di UserRepository
            Log.d("PhysicalDataViewModel", "Token retrieved from repository: $token")
            Log.d("PhysicalDataViewModel", "UserId retrieved from repository: $userId")

            // Tambahkan userId ke request
            val updatedRequest = request.copy(userId = userId)
            updatePhysicalData(token, updatedRequest)
        }
    }

    private fun updatePhysicalData(token: String, request: UpdatePhysicalRequest) {
        Log.d("PhysicalDataViewModel", "Token sent to server: $token")

        val client = ApiConfig.getApiService(token).updatePhysical(request)
        client.enqueue(object : Callback<UpdatePhysicalResponse> {
            override fun onResponse(call: Call<UpdatePhysicalResponse>, response: Response<UpdatePhysicalResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _updatePhysicalResult.postValue(Result.success(responseBody))
                    } else {
                        _updatePhysicalResult.postValue(Result.failure(Throwable("Response body is null")))
                    }
                } else {
                    _updatePhysicalResult.postValue(Result.failure(Throwable(response.errorBody()?.string() ?: "Unknown error")))
                }
            }

            override fun onFailure(call: Call<UpdatePhysicalResponse>, t: Throwable) {
                _updatePhysicalResult.postValue(Result.failure(t))
            }
        })
    }
}
