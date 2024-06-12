package com.dicoding.kaloriku.data.pref

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository private constructor(
    private val userPreference: UserPreference,
) {

    suspend fun saveSession(user: UserModel) {
        Log.d("UserRepository", "Before saving session: $user")
        userPreference.saveSession(user)
        Log.d("UserRepository", "After saving session: $user")
    }

    suspend fun saveToken(token: String) {
        Log.d("UserRepository", "Before saving token: $token")
        userPreference.saveToken(token)
        Log.d("UserRepository", "After saving token: $token")
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun getToken(): Flow<String> {
        return userPreference.getToken()
    }
    fun getUserId(): Flow<String> {
        return userPreference.getSession().map { it.userId }
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}
