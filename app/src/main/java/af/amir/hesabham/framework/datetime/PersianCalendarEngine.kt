package af.amir.hesabham.framework.datetime

import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.framework.datetime.model.AppDateTime
import android.util.Log
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import saman.zamani.persiandate.PersianDate
import saman.zamani.persiandate.PersianDateFormat
import saman.zamani.persiandate.PersianDateFormat.PersianDateNumberCharacter
import java.util.Locale

class PersianCalendarEngine : CalendarEngine {

    override fun now(): AppDateTime {
        return PersianDate().toAppDateTime()
    }

    override fun fromInstant(instant: Instant, systemZoneId: ZoneId): AppDateTime {
        return instant.toPersianDate().toAppDateTime()
    }

    override fun toInstant(dateTime: AppDateTime, systemZoneId: ZoneId): Instant {
        val epochMilli = dateTime.toPersianDate().time
        return Instant.ofEpochMilli(epochMilli)
    }

    override fun addDays(date: AppDate, days: Int): AppDate {
        val pDate = date.toPersianDate()
        val newPersianDate = pDate.addDays(days)
        return newPersianDate.toAppDate()
    }

    override fun addMonths(date: AppDate, months: Int): AppDate {
        val pDate = date.toPersianDate()
        val newPersianDate = pDate.addMonths(months)
        return newPersianDate.toAppDate()
    }

    override fun addYears(date: AppDate, years: Int): AppDate {
        val pDate = date.toPersianDate()
        val newPersianDate = pDate.addYears(years)
        return newPersianDate.toAppDate()
    }

    override fun subDays(date: AppDate, days: Int): AppDate {
        val pDate = date.toPersianDate()
        val newPersianDate = pDate.subDays(days)
        return newPersianDate.toAppDate()
    }

    override fun subMonths(date: AppDate, months: Int): AppDate {
        val pDate = date.toPersianDate().apply { shDay = 1 }
        val newPersianDate = pDate.subMonths(months)
        return newPersianDate.toAppDate()
    }

    override fun subYears(date: AppDate, years: Int): AppDate {
        val pDate = date.toPersianDate()
        val newPersianDate = pDate.subYears(years)
        return newPersianDate.toAppDate()
    }

    override fun startOfWeek(date: AppDate): AppDate {
        val pDate = date.toPersianDate()
        val newPersianDate = pDate.subDays(pDate.dayOfWeek())
        return newPersianDate.toAppDate()
    }

    override fun endOfWeek(date: AppDate): AppDate {
        val pDate = date.toPersianDate()
        val daysToAdd = 6 - pDate.dayOfWeek()
        val newPersianDate = pDate.addDays(daysToAdd)
        return newPersianDate.toAppDate()
    }


    override fun daysInMonth(year: Int, month: Int): Int {
        val pDate = PersianDate().apply {
            shMonth = month
            shYear = year
        }
        return pDate.monthDays
    }

    override fun isLeapYear(year: Int): Boolean {
        return PersianDate().apply { shYear = year }.isLeap
    }

    override fun monthName(month: Int, locale: Locale): String {
        val pDate = PersianDate().apply { shMonth = month }
        return if (locale == Locale("fa")) pDate.monthName() else pDate.FinglishMonthName()
    }


    override fun formatDate(date: AppDate, locale: Locale): String {
        val pDate = date.toPersianDate()
        val monthName = if (locale == Locale("fa")) pDate.monthName else pDate.FinglishMonthName()
        val formatted = "${date.year} " + monthName + " " + date.day
        return formatted
    }

    override fun formatDateTime(dateTime: AppDateTime, locale: Locale): String {
        val formattedDate = formatDate(dateTime.date,locale)
         val formattedTime = PersianDateFormat("H:i").format(dateTime.toPersianDate())
        return "$formattedDate , $formattedTime"
    }

    override fun formatTime(dateTime: AppDateTime, locale: Locale): String {
        val character =
            if (locale == Locale("fa")) PersianDateNumberCharacter.FARSI else PersianDateNumberCharacter.ENGLISH
        val formatter = PersianDateFormat("H:i", character)
        return formatter.format(dateTime.toPersianDate())
    }

    override fun getDaysBetween(start: AppDate, end: AppDate): Long {
        val daysBetween = start.toPersianDate().getDayUntilToday(end.toPersianDate())
        return daysBetween
    }


    //Mappers

    private fun Instant.toPersianDate(): PersianDate {
        return PersianDate(toEpochMilli())
    }


    private fun AppDateTime.toPersianDate(): PersianDate {
        val h = this.hour
        val m = this.minute
        return PersianDate().apply {
            shYear = date.year
            shMonth = date.month
            shDay = date.day
            hour = h
            minute = m
        }
    }

    private fun PersianDate.toAppDateTime(): AppDateTime {
        return AppDateTime(
            date = AppDate(shDay, shMonth, shYear),
            hour = hour,
            minute = minute
        )
    }

    private fun PersianDate.toAppDate(): AppDate {
        return AppDate(shDay, shMonth, shYear)
    }

    private fun AppDate.toPersianDate(): PersianDate {
        return PersianDate().apply {
            shYear = year
            shMonth = month
            shDay = day
        }
    }

}