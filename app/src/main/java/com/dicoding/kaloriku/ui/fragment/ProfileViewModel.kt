package com.dicoding.kaloriku.ui.fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.pref.PhysicalDataPreferences
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.data.response.UpdatePhysicalResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val physicalDataPreferences: PhysicalDataPreferences
) : ViewModel() {

    private val _physicalData = MutableLiveData<UpdatePhysicalRequest>()
    val physicalData: LiveData<UpdatePhysicalRequest> = _physicalData

    private val _updateResult = MutableLiveData<Result<UpdatePhysicalResponse>>()
    val updateResult: LiveData<Result<UpdatePhysicalResponse>> = _updateResult

    init {
        // Memuat data lokal saat ProfileViewModel diinisialisasi
        val physicalData = physicalDataPreferences.getPhysicalData()
        _physicalData.value = physicalData
    }

    fun loadPhysicalData() {
        viewModelScope.launch {
            try {
                val token = userRepository.getToken().first()
                val userId = userRepository.getUserId().first()
                val physicalData = userRepository.getPhysicalData(userId, token)
                _physicalData.value = physicalData
            } catch (e: Exception) {
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
                _physicalData.value = request // Update local LiveData after a successful update

                // Simpan data lokal setelah berhasil mengupdate di server
                physicalDataPreferences.savePhysicalData(request)
            } catch (e: Exception) {
                _updateResult.value = Result.failure(e)
            }
        }
    }
}

