package com.dicoding.kaloriku.ui.viewmodelactivity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.kaloriku.data.dao.FoodItemDao
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.data.response.FoodItem
import com.dicoding.kaloriku.data.response.FoodItemEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(
    private val userRepository: UserRepository,
    private val foodItemDao: FoodItemDao
) : ViewModel() {

    private val _selectedDate = MutableLiveData<Date>()
    val selectedDate: LiveData<Date> = _selectedDate

    private val _breakfastItems = MutableLiveData<List<FoodItem>>()
    val breakfastItems: LiveData<List<FoodItem>> = _breakfastItems

    private val _lunchItems = MutableLiveData<List<FoodItem>>()
    val lunchItems: LiveData<List<FoodItem>> = _lunchItems

    private val _dinnerItems = MutableLiveData<List<FoodItem>>()
    val dinnerItems: LiveData<List<FoodItem>> = _dinnerItems

    init {
        _selectedDate.value = Date()
        loadFoodItemsForDate(_selectedDate.value!!)
    }

    fun setDate(date: Date) {
        _selectedDate.value = date
        loadFoodItemsForDate(date)
        Log.d("LMAOOOOOOOOOOO", "Initial selected date: $date")
    }

    fun loadFoodItemsForDate(date: Date) {
        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        viewModelScope.launch {
            _breakfastItems.value = foodItemDao.getFoodItemsForMeal(formattedDate, "Breakfast").map { it.toFoodItem() }
            _lunchItems.value = foodItemDao.getFoodItemsForMeal(formattedDate, "Lunch").map { it.toFoodItem() }
            _dinnerItems.value = foodItemDao.getFoodItemsForMeal(formattedDate, "Dinner").map { it.toFoodItem() }

            Log.d("MainViewModel", "Loading food items for date: $date, formattedDate: $formattedDate")
        }
    }

    fun addFoodItemForDate(food: FoodItem, currentDate: Date?, mealType: String) {
        val date = currentDate ?: Date()

        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
        Log.d("JIKAAAAAAA", "Selected date: $date")
        val foodItemEntity = FoodItemEntity(
            name = food.name,
            calories = food.calories,
            carbohydrate = food.carbohydrate,
            fat = food.fat,
            image = food.image,
            proteins = food.proteins,
            date = formattedDate,
            mealType = mealType
        )

        viewModelScope.launch {
            foodItemDao.insert(foodItemEntity)
            loadFoodItemsForDate(date)
        }
    }

    fun getSession() = userRepository.getSession().asLiveData()

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    private fun FoodItemEntity.toFoodItem(): FoodItem {
        return FoodItem(
            id = this.id.toInt(),
            name = this.name,
            calories = this.calories,
            carbohydrate = this.carbohydrate,
            fat = this.fat,
            image = this.image,
            proteins = this.proteins
        )
    }
}