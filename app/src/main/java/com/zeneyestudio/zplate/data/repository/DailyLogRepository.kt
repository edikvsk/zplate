package com.zeneyestudio.zplate.data.repository

import com.zeneyestudio.zplate.data.db.DailyLogDao
import com.zeneyestudio.zplate.data.model.DailyLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyLogRepository @Inject constructor(
    private val dailyLogDao: DailyLogDao
) {
    fun getLogsByDate(date: String): Flow<List<DailyLog>> = dailyLogDao.getLogsByDate(date)

    fun getLogsByDateAndType(date: String, mealType: String): Flow<List<DailyLog>> =
        dailyLogDao.getLogsByDateAndType(date, mealType)

    suspend fun insertLog(log: DailyLog): Long = dailyLogDao.insertLog(log)

    suspend fun deleteLog(log: DailyLog) = dailyLogDao.deleteLog(log)

    suspend fun deleteLogById(logId: Long) = dailyLogDao.deleteLogById(logId)

    fun getLogsBetweenDates(startDate: String, endDate: String): Flow<List<DailyLog>> =
        dailyLogDao.getLogsBetweenDates(startDate, endDate)

    fun getTodayDate(): String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

    fun getDateString(date: LocalDate): String = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
