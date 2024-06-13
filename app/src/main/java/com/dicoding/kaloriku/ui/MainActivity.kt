package com.dicoding.kaloriku.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.kaloriku.R
import com.dicoding.kaloriku.ui.fragment.DashboardFragment
import com.dicoding.kaloriku.ui.fragment.ProfileFragment
import com.dicoding.kaloriku.ui.fragment.ProgressFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, ProgressFragment())
            .commit()

        setupBottomNavigation()
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
                // Kasus untuk ProfileFragment dan SettingsFragment
                else -> false
            }
        }
    }
}
