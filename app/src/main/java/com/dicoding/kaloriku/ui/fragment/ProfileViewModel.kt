package com.dicoding.kaloriku.ui.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.response.ProfileResponse
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.data.response.UpdatePhysicalResponse
import com.dicoding.kaloriku.data.response.UserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Response


class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _physicalData = MutableLiveData<UserProfile?>()
    val physicalData: LiveData<UserProfile?> = _physicalData

    private val _updateResult = MutableLiveData<Result<UpdatePhysicalResponse>>()
    val updateResult: LiveData<Result<UpdatePhysicalResponse>> = _updateResult

    fun loadPhysicalData() {
        viewModelScope.launch {
            try {
                val token = userRepository.getToken().first()
                val userId = userRepository.getUserId().first()
                val response: Response<ProfileResponse> = userRepository.getPhysicalData(token, userId)

                if (response.isSuccessful) {
                    val profileResponse = response.body()
                    _physicalData.value = profileResponse?.user
                } else {
                    _physicalData.value = null
                    Log.e("ProfileViewModel", "Error loading physical data: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _physicalData.value = null
                Log.e("ProfileViewModel", "Error loading physical data", e)
            }
        }
    }

    fun updatePhysicalData(request: UpdatePhysicalRequest) {
        viewModelScope.launch {
            try {
                val token = userRepository.getToken().first()
                val userId = userRepository.getUserId().first()
                val response = userRepository.updatePhysicalData(request.copy(userId = userId), token)

                _updateResult.value = Result.success(response)
                _physicalData.value = request.toUserProfile()
            } catch (e: Exception) {
                _updateResult.value = Result.failure(e)
            }
        }
    }

    private fun UpdatePhysicalRequest.toUserProfile(): UserProfile {
        return UserProfile(
            userId = this.userId,
            username = this.username,
            email = "",
            weight = this.weight,
            height = this.height,
            gender = this.gender,
            birthdate = this.birthdate,
            age = null
        )
    }
}