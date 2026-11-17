package af.amir.hesabham.framework.datetime

import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.framework.datetime.model.AppDateTime
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import java.util.Locale

interface CalendarEngine {

    fun now(): AppDateTime

    fun fromInstant(
        instant: Instant,
        systemZoneId: ZoneId = ZoneId.systemDefault(),
    ): AppDateTime

    fun toInstant(
        dateTime: AppDateTime,
        systemZoneId: ZoneId = ZoneId.systemDefault(),
    ): Instant

    fun toInstant(
        dateTime: AppDate,
        systemZoneId: ZoneId = ZoneId.systemDefault(),
    ): Instant = toInstant(AppDateTime(dateTime, 0, 0))


    // Arithmetic

    fun addDays(
        date: AppDate,
        days: Int,
    ): AppDate

    fun addMonths(
        date: AppDate,
        months: Int,
    ): AppDate

    fun addYears(
        date: AppDate,
        years: Int,
    ): AppDate

    fun subDays(
        date: AppDate,
        days: Int,
    ): AppDate

    fun subMonths(
        date: AppDate,
        months: Int,
    ): AppDate

    fun subYears(
        date: AppDate,
        years: Int,
    ): AppDate

    // Month

    fun startOfMonth(
        date: AppDate,
    ): AppDate = date.copy(day = 1)

    fun endOfMonth(
        date: AppDate,
    ): AppDate = date.copy(day = daysInMonth(date.year, date.month))


    // Week

    fun startOfWeek(
        date: AppDate,
    ): AppDate

    fun endOfWeek(
        date: AppDate,
    ): AppDate

    // Year

    fun startOfYear(
        date: AppDate,
    ): AppDate = date.copy(day = 1, month = 1)

    fun endOfYear(
        date: AppDate,
    ): AppDate = date.copy(month = 12, day = daysInMonth(date.year, 12))

    // Information

    fun daysInMonth(
        year: Int,
        month: Int,
    ): Int

    fun isLeapYear(
        year: Int,
    ): Boolean


    // Display

    fun monthName(
        month: Int,
        locale: Locale,
    ): String


    fun formatDate(
        date: AppDate,
        locale: Locale,
    ): String

    fun formatDateTime(
        dateTime: AppDateTime,
        locale: Locale,
    ): String

    fun formatTime(
        dateTime: AppDateTime,
        locale: Locale,
    ): String


    fun getDaysBetween(start: AppDate, end: AppDate): Long
    fun getDaysBetween(start: Instant, end: Instant): Long {
        return getDaysBetween(fromInstant(start).date, fromInstant(end).date)
    }

}