package com.dicoding.kaloriku.data.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.dicoding.kaloriku.data.response.FoodItemEntity
import com.dicoding.kaloriku.data.response.DailyConsumption

@Database(entities = [FoodItemEntity::class, DailyConsumption::class],
	version = 3,
	exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

	abstract fun foodItemDao(): FoodItemDao
	abstract fun dailyConsumptionDao(): DailyConsumptionDao

	companion object {
		@Volatile
		private var INSTANCE: AppDatabase? = null

		fun getDatabase(context: Context): AppDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"app_database"
				).fallbackToDestructiveMigration().build()
				INSTANCE = instance
				instance
			}
		}
	}
}