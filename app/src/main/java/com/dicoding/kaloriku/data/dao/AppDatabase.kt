package com.dicoding.kaloriku.data.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dicoding.kaloriku.data.response.FoodItemEntity

@Database(entities = [FoodItemEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

	abstract fun foodItemDao(): FoodItemDao

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
