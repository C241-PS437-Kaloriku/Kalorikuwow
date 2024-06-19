package com.dicoding.kaloriku.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.kaloriku.R
import com.dicoding.kaloriku.ui.fragment.DashboardFragment
import com.dicoding.kaloriku.ui.fragment.ProfileFragment
import com.dicoding.kaloriku.ui.fragment.ProgressFragment
import com.dicoding.kaloriku.ui.helper.FoodRecommendationHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var foodRecommendationHelper: FoodRecommendationHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ProgressFragment())
            .commit()

        foodRecommendationHelper = FoodRecommendationHelper(this)

        val weight = 75f
        val height = 175f
        val age = 20
        val goal = "maintain"

        foodRecommendationHelper.getFoodRecommendations(weight, height, age, goal) { recommendations ->
            recommendations.forEach { food ->
                Log.d("FoodRecommendation", "Food: ${food.name}, Calories: ${food.calories}")

                setupBottomNavigation()
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.progressFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ProgressFragment())
                        .commit()
                    true
                }
                R.id.dashboardFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, DashboardFragment())
                        .commit()
                    true
                }
                R.id.profileFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ProfileFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }
}
