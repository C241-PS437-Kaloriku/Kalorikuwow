package com.dicoding.kaloriku.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dicoding.kaloriku.data.response.FoodItem
import com.dicoding.kaloriku.data.response.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
//	@Insert
//	suspend fun insert(foodItemEntity: FoodItemEntity)

	@Update
	suspend fun update(foodItemEntity: FoodItemEntity)

	@Delete
	suspend fun delete(foodItemEntity: FoodItemEntity)

	@Query("SELECT * FROM food_items")
	suspend fun getAllFoodItems(): List<FoodItemEntity>

	@Query("SELECT * FROM food_items")
	fun getAllFoodItemsFlow(): Flow<List<FoodItemEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(foodItem: FoodItemEntity)

	@Query("SELECT * FROM food_items WHERE date = :date AND mealtype = :mealType")
	suspend fun getFoodItemsForMeal(date: String, mealType: String): List<FoodItemEntity>
}
