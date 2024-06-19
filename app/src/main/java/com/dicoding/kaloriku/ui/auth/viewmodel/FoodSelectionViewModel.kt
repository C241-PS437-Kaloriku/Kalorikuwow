package com.dicoding.kaloriku.ui.auth.viewmodel

import FoodItem
import FoodItemDao
import FoodItemEntity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch



// FoodSelectionViewModel.kt
class FoodSelectionViewModel(private val foodItemDao: FoodItemDao) : ViewModel() {

    fun insertFoodItem(foodItemEntity: FoodItemEntity) {
        viewModelScope.launch {
            foodItemDao.insert(foodItemEntity)
        }
    }
}