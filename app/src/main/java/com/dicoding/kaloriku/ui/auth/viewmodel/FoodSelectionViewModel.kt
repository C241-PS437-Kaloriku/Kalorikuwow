package com.dicoding.kaloriku.ui.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.dao.FoodItemDao
import com.dicoding.kaloriku.data.response.FoodItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FoodSelectionViewModel(private val foodItemDao: FoodItemDao) : ViewModel() {

    val allFoodItems: Flow<List<FoodItemEntity>> = foodItemDao.getAllFoodItemsFlow()

    fun insertFoodItem(foodItemEntity: FoodItemEntity) {
        viewModelScope.launch {
            foodItemDao.insert(foodItemEntity)
        }
    }
}
