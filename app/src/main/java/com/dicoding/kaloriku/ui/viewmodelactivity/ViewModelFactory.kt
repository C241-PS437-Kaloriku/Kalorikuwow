package com.dicoding.kaloriku.ui.viewmodelactivity

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.kaloriku.data.dao.DailyConsumptionDao
import com.dicoding.kaloriku.data.dao.FoodItemDao
import com.dicoding.kaloriku.data.di.Injection
import com.dicoding.kaloriku.data.pref.UserRepository
import com.dicoding.kaloriku.ui.fragment.viewmodelfrag.BMIViewModel
import com.dicoding.kaloriku.ui.fragment.viewmodelfrag.FoodSelectionViewModel
import com.dicoding.kaloriku.ui.auth.viewmodelauth.LoginViewModel
import com.dicoding.kaloriku.ui.fragment.viewmodelfrag.ProgressViewModel
import com.dicoding.kaloriku.ui.fragment.viewmodelfrag.ProfileViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val foodItemDao: FoodItemDao,
    private val dailyConsumptionDao: DailyConsumptionDao
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(ProgressViewModel::class.java) -> {
                ProgressViewModel(dailyConsumptionDao) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, foodItemDao) as T
            }
            modelClass.isAssignableFrom(PhysicalDataViewModel::class.java) -> {
                PhysicalDataViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(BMIViewModel::class.java) -> {
                BMIViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(FoodSelectionViewModel::class.java) -> {
                FoodSelectionViewModel(foodItemDao) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideUserRepository(context),
                    Injection.provideFoodItemDao(context),
                    Injection.provideDailyConsumptionDao(context)
                ).also { INSTANCE = it }
            }
        }
    }
}