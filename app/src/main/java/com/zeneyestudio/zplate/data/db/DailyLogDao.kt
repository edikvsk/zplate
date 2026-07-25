package com.zeneyestudio.zplate.data.db

import androidx.room.*
import com.zeneyestudio.zplate.data.model.DailyLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs WHERE date = :date ORDER BY timestamp ASC")
    fun getLogsByDate(date: String): Flow<List<DailyLog>>

    @Query("SELECT * FROM daily_logs WHERE date = :date AND mealType = :mealType ORDER BY timestamp ASC")
    fun getLogsByDateAndType(date: String, mealType: String): Flow<List<DailyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DailyLog): Long

    @Delete
    suspend fun deleteLog(log: DailyLog)

    @Query("DELETE FROM daily_logs WHERE id = :logId")
    suspend fun deleteLogById(logId: Long)

    @Query("SELECT * FROM daily_logs WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, timestamp ASC")
    fun getLogsBetweenDates(startDate: String, endDate: String): Flow<List<DailyLog>>
}
