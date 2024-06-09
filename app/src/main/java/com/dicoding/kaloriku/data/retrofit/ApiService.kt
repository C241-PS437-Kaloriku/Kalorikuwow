package com.dicoding.kaloriku.data.retrofit

import com.dicoding.kaloriku.data.LoginRequest
import com.dicoding.kaloriku.data.LoginResponse
import com.dicoding.kaloriku.data.RegisterRequest
import com.dicoding.kaloriku.data.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @POST("register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>
}