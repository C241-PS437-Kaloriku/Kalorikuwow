package com.dicoding.kaloriku.data.pref

import android.util.Log
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.data.response.UpdatePhysicalResponse
import com.dicoding.kaloriku.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService // Added ApiService as a constructor parameter
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

    suspend fun hasPhysicalData(): Boolean {
        return userPreference.hasPhysicalData()
    }

    suspend fun getPhysicalData(userId: String, token: String): UpdatePhysicalRequest {
        // Implement the logic to fetch physical data using userId and token
        // Example:
        val response = apiService.getPhysicalData(token, userId) // Replace with actual API call
        return if (response.isSuccessful) {
            response.body() ?: throw Exception("No data available")
        } else {
            throw Exception(response.errorBody()?.string() ?: "Unknown error")
        }
    }

    suspend fun updatePhysicalData(request: UpdatePhysicalRequest, token: String): UpdatePhysicalResponse {
        return apiService.updatePhysical(token, request).body()
            ?: throw Exception("Failed to update physical data")
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService // Added apiService parameter
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}
