package com.dicoding.kaloriku.ui.viewmodelactivity

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
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class PhysicalDataViewModel(private val repository: UserRepository) : ViewModel() {

    private val _updatePhysicalResult = MutableLiveData<Result<UpdatePhysicalResponse>>()
    val updatePhysicalResult: LiveData<Result<UpdatePhysicalResponse>> = _updatePhysicalResult

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
                val errorMessage = if (e is HttpException) {
                    e.response()?.errorBody()?.string()?.let { responseBody ->
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            jsonResponse.getString("message")
                        } catch (jsonException: JSONException) {
                            e.message()
                        }
                    }
                } else {
                    e.message
                }
                _updatePhysicalResult.postValue(Result.failure(Exception(errorMessage)))
            }
        }
    }

}
