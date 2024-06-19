package com.dicoding.kaloriku.data.retrofit

import org.checkerframework.checker.units.qual.A
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://model-api-nn3iqpbana-et.a.run.app"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: FoodRecommendationApi by lazy {
        retrofit.create(FoodRecommendationApi::class.java)
    }
}