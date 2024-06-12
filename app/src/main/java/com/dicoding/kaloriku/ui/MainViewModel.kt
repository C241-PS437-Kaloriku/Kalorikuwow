package com.dicoding.kaloriku.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.pref.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getSession() = userRepository.getSession().asLiveData()

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
