package com.zeneyestudio.zplate.util

import java.time.LocalTime

object TimeHelper {

    data class TimeRange(
        val startHour: Int,
        val startMinute: Int,
        val endHour: Int,
        val endMinute: Int
    ) {
        fun contains(time: LocalTime): Boolean {
            val start = LocalTime.of(startHour, startMinute)
            val end = LocalTime.of(endHour, endMinute)
            return if (start.isBefore(end) || start == end) {
                !time.isBefore(start) && !time.isAfter(end)
            } else {
                !time.isBefore(start) || !time.isAfter(end)
            }
        }
    }

    fun getCurrentMealType(
        breakfast: TimeRange,
        lunch: TimeRange,
        dinner: TimeRange
    ): String {
        val now = LocalTime.now()
        return when {
            breakfast.contains(now) -> "завтрак"
            lunch.contains(now) -> "обед"
            dinner.contains(now) -> "ужин"
            else -> "ужин"
        }
    }

    fun getCurrentMealEmoji(mealType: String): String {
        return when (mealType) {
            "завтрак" -> "Завтрак"
            "обед" -> "Обед"
            "ужин" -> "Ужин"
            "перекус" -> "Перекус"
            else -> ""
        }
    }

    fun getMealDisplayName(mealType: String): String {
        return when (mealType) {
            "завтрак" -> "Завтрак"
            "обед" -> "Обед"
            "ужин" -> "Ужин"
            "перекус" -> "Перекус"
            else -> mealType
        }
    }

    fun getMealTimeRangeText(range: TimeRange): String {
        return String.format(
            "%02d:%02d — %02d:%02d",
            range.startHour, range.startMinute,
            range.endHour, range.endMinute
        )
    }
}
