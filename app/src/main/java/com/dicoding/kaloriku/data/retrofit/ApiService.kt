package com.dicoding.kaloriku.data.retrofit

import com.dicoding.kaloriku.data.response.LoginRequest
import com.dicoding.kaloriku.data.response.LoginResponse
import com.dicoding.kaloriku.data.response.RegisterRequest
import com.dicoding.kaloriku.data.response.RegisterResponse
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.data.response.UpdatePhysicalResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {

    @POST("register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @PUT("update-physical")
    fun updatePhysical(
        @Body updatePhysicalRequest: UpdatePhysicalRequest
    ): Call<UpdatePhysicalResponse>

}