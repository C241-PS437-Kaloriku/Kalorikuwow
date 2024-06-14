package com.dicoding.kaloriku.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.data.response.UpdatePhysicalResponse
import com.dicoding.kaloriku.data.pref.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PhysicalDataViewModel(private val repository: UserRepository) : ViewModel() {

    private val _updatePhysicalResult = MutableLiveData<Result<UpdatePhysicalResponse>>()
    val updatePhysicalResult: LiveData<Result<UpdatePhysicalResponse>> = _updatePhysicalResult

    private val _physicalData = MutableLiveData<UpdatePhysicalRequest>()
    val physicalData: LiveData<UpdatePhysicalRequest> = _physicalData

    fun getTokenAndUpdatePhysicalData(request: UpdatePhysicalRequest) {
        viewModelScope.launch {
            val token = repository.getToken().first()
            val userId = repository.getUserId().first()
            Log.d("PhysicalDataViewModel", "Token retrieved from repository: $token")
            Log.d("PhysicalDataViewModel", "UserId retrieved from repository: $userId")

            val updatedRequest = request.copy(userId = userId)
            updatePhysicalData(token, updatedRequest)
        }
    }

    private fun updatePhysicalData(token: String, request: UpdatePhysicalRequest) {
        viewModelScope.launch {
            try {
                val response = repository.updatePhysicalData(request, token)
                _updatePhysicalResult.postValue(Result.success(response))
            } catch (e: Exception) {
                _updatePhysicalResult.postValue(Result.failure(e))
            }
        }
    }

    fun fetchPhysicalData() {
        viewModelScope.launch {
            try {
                val token = repository.getToken().first()
                val userId = repository.getUserId().first()
                val physicalData = repository.getPhysicalData(userId, token)
                _physicalData.postValue(physicalData)
            } catch (e: Exception) {
                Log.e("PhysicalDataViewModel", "Failed to fetch physical data", e)
            }
        }
    }
}
