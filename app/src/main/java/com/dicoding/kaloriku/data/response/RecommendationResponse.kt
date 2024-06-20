package com.dicoding.kaloriku.data.response

import androidx.room.Entity
import androidx.room.PrimaryKey
data class FoodRecommendationRequest(
	val weight: Int,
	val height: Int,
	val age: Int,
	val goal: String
)

@Entity(tableName = "food_items")
data class FoodItemEntity(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val name: String,
	val calories: Double,
	val carbohydrate: Double,
	val fat: Double,
	val image: String,
	val proteins: Double,
	val date: String,
	val mealType: String,
)
@Entity(tableName = "daily_consumption")
data class DailyConsumption(
	@PrimaryKey val date: String,
	val calories: Double,
	val carbs: Double,
	val proteins: Double,
	val fats: Double
)

data class FoodItem(
	val id: Int,
	val name: String,
	val calories: Double,
	val carbohydrate: Double,
	val fat: Double,
	val image: String,
	val proteins: Double

)

data class FoodRecommendationResponse(
	val daily_calories_needed: Float,
	val recommended_meals: List<FoodItem>
)
