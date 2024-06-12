package com.dicoding.kaloriku.data.retrofit

import com.dicoding.kaloriku.data.response.BMIResponse
import com.dicoding.kaloriku.data.response.LoginRequest
import com.dicoding.kaloriku.data.response.LoginResponse
import com.dicoding.kaloriku.data.response.RegisterRequest
import com.dicoding.kaloriku.data.response.RegisterResponse
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.data.response.UpdatePhysicalResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Call<RegisterResponse>

    @GET("bmical")
    suspend fun calculateBMI(@Header("Authorization") token: String, @Query("userId") userId: String): Response<BMIResponse>

    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @PUT("update-physical")
    fun updatePhysical(
        @Body updatePhysicalRequest: UpdatePhysicalRequest
    ): Call<UpdatePhysicalResponse>

}