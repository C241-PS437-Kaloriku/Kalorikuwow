package com.dicoding.kaloriku.data.di

import android.content.Context
import com.dicoding.kaloriku.data.pref.PhysicalDataPreferences
import com.dicoding.kaloriku.data.pref.UserPreference
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.pref.dataStore
import com.dicoding.kaloriku.data.retrofit.ApiConfig

class Injection {
    companion object {
        fun provideUserRepository(context: Context): UserRepository {
            val pref = UserPreference.getInstance(context.dataStore)
            val apiService = ApiConfig.getApiService() // Get ApiService instance
            return UserRepository.getInstance(pref, apiService) // Pass ApiService to UserRepository
        }

        fun providePhysicalDataPreferences(context: Context): PhysicalDataPreferences {
            return PhysicalDataPreferences.getInstance(context)
        }
    }
}
