package com.dicoding.kaloriku.ui.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.response.PhotoProfileResponse
import com.dicoding.kaloriku.data.response.ProfileResponse
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.data.response.UpdatePhysicalResponse
import com.dicoding.kaloriku.data.response.UserProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _physicalData = MutableLiveData<UserProfile?>()
    val physicalData: LiveData<UserProfile?> = _physicalData

    private val _updateResult = MutableLiveData<Result<UpdatePhysicalResponse>>()
    val updateResult: LiveData<Result<UpdatePhysicalResponse>> = _updateResult

    private val _photoUploadResult = MutableLiveData<Result<PhotoProfileResponse>>()
    val photoUploadResult: LiveData<Result<PhotoProfileResponse>> = _photoUploadResult

    fun loadPhysicalData() {
        viewModelScope.launch {
            try {
                val token = userRepository.getToken().first()
                val userId = userRepository.getUserId().first()
                val response: Response<ProfileResponse> = userRepository.getPhysicalData(token, userId)

                if (response.isSuccessful) {
                    val profileResponse = response.body()
                    if (profileResponse != null) {
                        Log.d("ProfileViewModel", "Profile picture URL: ${profileResponse.user?.profilePictureUrl}")
                        _physicalData.value = profileResponse.user
                    } else {
                        _physicalData.value = null
                        Log.e("ProfileViewModel", "Profile response is null")
                    }
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
                loadPhysicalData()
            } catch (e: Exception) {
                _updateResult.value = Result.failure(e)
            }
        }
    }

    fun uploadPhotoProfile(photoPath: String) {
        viewModelScope.launch {
            try {
                val file = File(photoPath)
                val mimeType = getMimeType(file.name)
                if (mimeType != "image/jpeg" && mimeType != "image/png") {
                    throw Exception("File must be JPEG or PNG")
                }

                val token = userRepository.getToken().first()
                val userId = userRepository.getUserId().first()
                val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("profilePicture", file.name, requestFile)
                val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())

                // Logging the details
                Log.d("ProfileViewModel", "Token: $token")
                Log.d("ProfileViewModel", "UserId as query: $userId")
                Log.d("ProfileViewModel", "UserId as form-data: ${userIdPart.string()}")
                Log.d("ProfileViewModel", "Photo file size: ${file.length()} bytes")

                val response = userRepository.uploadPhotoProfile(token, userId, userIdPart, body)

                if (response.isSuccessful) {
                    _photoUploadResult.value = Result.success(response.body()!!)
                    loadPhysicalData()
                } else {
                    val errorMessage = "Failed to upload photo: ${response.code()} - ${response.errorBody()?.string() ?: "Unknown error"}"
                    _photoUploadResult.value = Result.failure(Exception(errorMessage))
                    Log.e("ProfileViewModel", errorMessage)
                }
            } catch (e: Exception) {
                _photoUploadResult.value = Result.failure(e)
                Log.e("ProfileViewModel", "Error uploading photo", e)
            }
        }
    }

    private fun RequestBody.string(): String {
        val buffer = okio.Buffer()
        this.writeTo(buffer)
        return buffer.readUtf8()
    }

    private fun getMimeType(fileName: String): String? {
        return when (fileName.substringAfterLast('.', "").lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> null
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
            age = null,
            profilePictureUrl = this.profilePictureUrl
        )
    }
}