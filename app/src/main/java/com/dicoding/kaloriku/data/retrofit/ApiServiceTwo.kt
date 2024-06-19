package com.dicoding.kaloriku.data.retrofit

import FoodRecommendationRequest
import FoodRecommendationResponse
import com.dicoding.kaloriku.data.response.BMIResponse
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
interface FoodRecommendationApi {
    @POST("/predict")
    fun getFoodRecommendations(@Body request: FoodRecommendationRequest): Call<FoodRecommendationResponse>
}