package com.dicoding.kaloriku.data.pref

import android.content.Context
import android.content.SharedPreferences
import com.dicoding.kaloriku.data.response.UpdatePhysicalRequest

class PhysicalDataPreferences private constructor(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences("physical_data_preferences", Context.MODE_PRIVATE)

    fun savePhysicalData(physicalData: UpdatePhysicalRequest) {
        val editor = sharedPreferences.edit()
        editor.putInt("weight", physicalData.weight)
        editor.putInt("height", physicalData.height)
        editor.putString("gender", physicalData.gender)
        editor.putString("birthdate", physicalData.birthdate)
        editor.putString("username", physicalData.username)
        editor.apply()
    }

    fun getPhysicalData(): UpdatePhysicalRequest {
        val weight = sharedPreferences.getInt("weight", 0)
        val height = sharedPreferences.getInt("height", 0)
        val gender = sharedPreferences.getString("gender", "") ?: ""
        val birthdate = sharedPreferences.getString("birthdate", "") ?: ""
        val username = sharedPreferences.getString("username", "") ?: ""
        return UpdatePhysicalRequest(weight, height, gender, birthdate, userId = "", username)
    }


    companion object {
        @Volatile
        private var INSTANCE: PhysicalDataPreferences? = null

        fun getInstance(context: Context): PhysicalDataPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PhysicalDataPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
