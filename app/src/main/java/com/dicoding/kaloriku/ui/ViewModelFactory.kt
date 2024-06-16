package com.dicoding.kaloriku.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.kaloriku.data.di.Injection
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.ui.auth.viewmodel.BMIViewModel
import com.dicoding.kaloriku.ui.auth.viewmodel.LoginViewModel
import com.dicoding.kaloriku.ui.fragment.ProfileViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(PhysicalDataViewModel::class.java) -> {
                PhysicalDataViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(BMIViewModel::class.java) -> {
                BMIViewModel(userRepository) as T // Pass apiService here
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideUserRepository(context),
                ).also { INSTANCE = it }
            }
        }
    }
}
