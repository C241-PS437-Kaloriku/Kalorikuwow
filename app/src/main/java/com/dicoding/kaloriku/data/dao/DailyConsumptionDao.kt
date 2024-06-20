package com.dicoding.kaloriku.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.kaloriku.data.response.DailyConsumption
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyConsumptionDao {
    @Query("SELECT * FROM daily_consumption WHERE date = :date")
    fun getConsumptionForDate(date: String): Flow<DailyConsumption?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(consumption: DailyConsumption)
}