package com.dicoding.kaloriku.data.di

import android.content.Context
import com.dicoding.kaloriku.data.pref.UserPreference
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.pref.dataStore

class Injection {
    companion object {
        fun provideUserRepository(context: Context): UserRepository {
            val pref = UserPreference.getInstance(context.dataStore)
            return UserRepository.getInstance(pref)

        }
    }
}