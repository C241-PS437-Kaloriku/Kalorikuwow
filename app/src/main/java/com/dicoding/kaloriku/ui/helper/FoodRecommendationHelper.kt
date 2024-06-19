package com.dicoding.kaloriku.ui.helper

import FoodItem
import FoodRecommendationRequest
import FoodRecommendationResponse
import android.content.Context
import android.util.Log
import com.dicoding.kaloriku.data.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FoodRecommendationHelper(private val context: Context) {

    fun getFoodRecommendations(
        weight: Float,
        height: Float,
        age: Int,
        goal: String,
        callback: (List<FoodItem>) -> Unit
    ) {
        val request = FoodRecommendationRequest(weight, height, age, goal)
        RetrofitClient.api.getFoodRecommendations(request).enqueue(object : Callback<FoodRecommendationResponse> {
            override fun onResponse(
                call: Call<FoodRecommendationResponse>,
                response: Response<FoodRecommendationResponse>
            ) {
                if (response.isSuccessful) {
                    val recommendations = response.body()?.recommended_meals ?: emptyList()
                    callback(recommendations)
                } else {
                    Log.e("FoodRecommendationHelper", "API call failed with response code: ${response.code()}")
                    callback(emptyList())
                }
            }

            override fun onFailure(call: Call<FoodRecommendationResponse>, t: Throwable) {
                Log.e("FoodRecommendationHelper", "API call failed: ${t.message}")
                callback(emptyList())
            }
        })
    }
}