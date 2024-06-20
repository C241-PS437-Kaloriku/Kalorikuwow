package com.dicoding.kaloriku.data.retrofit

import com.dicoding.kaloriku.data.response.BMIResponse
import com.dicoding.kaloriku.data.response.FoodRecommendationRequest
import com.dicoding.kaloriku.data.response.FoodRecommendationResponse
import com.dicoding.kaloriku.data.response.LoginRequest
import com.dicoding.kaloriku.data.response.LoginResponse
import com.dicoding.kaloriku.data.response.PhotoProfileResponse
import com.dicoding.kaloriku.data.response.ProfileResponse
import com.dicoding.kaloriku.data.response.RegisterRequest
import com.dicoding.kaloriku.data.response.RegisterResponse
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest
import com.dicoding.kaloriku.data.response.UpdatePhysicalResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Call<RegisterResponse>

    @GET("bmical")
    suspend fun calculateBMI(
        @Header("Authorization") token: String,
        @Query("userId") userId: String): Response<BMIResponse>

    @POST("login")
    fun login(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @GET("profile")
    suspend fun getPhysicalData(
        @Header("Authorization") token: String,
        @Query("userId") userId: String
    ): Response<ProfileResponse>

    @PUT("profile")
    suspend fun updatePhysical(
        @Header("Authorization") token: String,
        @Body updatePhysicalRequest: UpdatePhysicalRequest
    ): Response<UpdatePhysicalResponse>

    @Multipart
    @POST("photoprofile")
    suspend fun uploadPhotoProfile(
        @Header("Authorization") token: String,
        @Query("userId") userId: String,
        @Part("userId") userIdPart: RequestBody,
        @Part profilePicture: MultipartBody.Part
    ): Response<PhotoProfileResponse>

    @POST("predict")
    fun getFoodRecommendations(
        @Body request: FoodRecommendationRequest
    ): Call<FoodRecommendationResponse>
}