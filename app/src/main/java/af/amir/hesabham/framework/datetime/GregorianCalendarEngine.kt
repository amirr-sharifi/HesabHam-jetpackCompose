package af.amir.hesabham.framework.datetime

import af.amir.hesabham.framework.datetime.model.AppDate
import af.amir.hesabham.framework.datetime.model.AppDateTime
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.Year
import org.threeten.bp.YearMonth
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

class GregorianCalendarEngine : CalendarEngine {

    override fun now(): AppDateTime {
        return LocalDateTime.now().toAppDateTime()
    }

    override fun fromInstant(instant: Instant, systemZoneId: ZoneId): AppDateTime {
        return LocalDateTime.ofInstant(instant, systemZoneId).toAppDateTime()
    }

    override fun toInstant(dateTime: AppDateTime, systemZoneId: ZoneId): Instant {
        return dateTime.toLocalDateTime().atZone(systemZoneId).toInstant()
    }

    override fun addMonths(date: AppDate, months: Int): AppDate {
        val gDate = date.toLocalDateTime()
        val newGregorianDate = gDate.plusMonths(months.toLong())
        return newGregorianDate.toAppDate()
    }

    override fun addDays(date: AppDate, days: Int): AppDate {
        val gDate = date.toLocalDateTime()
        val newGregorianDate = gDate.plusDays(days.toLong())
        return newGregorianDate.toAppDate()
    }

    override fun addYears(date: AppDate, years: Int): AppDate {
        val gDate = date.toLocalDateTime()
        val newGregorianDate = gDate.plusYears(years.toLong())
        return newGregorianDate.toAppDate()
    }

    override fun subDays(date: AppDate, days: Int): AppDate {
        val gDate = date.toLocalDateTime()
        val newGregorianDate = gDate.minusDays(days.toLong())
        return newGregorianDate.toAppDate()
    }

    override fun subMonths(date: AppDate, months: Int): AppDate {
        val gDate = date.toLocalDateTime()
        val newGregorianDate = gDate.minusMonths(months.toLong())
        return newGregorianDate.toAppDate()
    }

    override fun subYears(date: AppDate, years: Int): AppDate {
        val gDate = date.toLocalDateTime()
        val newGregorianDate = gDate.minusYears(years.toLong())
        return newGregorianDate.toAppDate()
    }

    override fun startOfWeek(date: AppDate): AppDate {
        val gDate = date.toLocalDateTime()
        val daysToSub = gDate.dayOfWeek.ordinal
        val newGregorianDate = gDate.minusDays(daysToSub.toLong())
        return newGregorianDate.toAppDate()
    }

    override fun endOfWeek(date: AppDate): AppDate {
        val gDate = date.toLocalDateTime()
        val daysToAdd = 6 - gDate.dayOfWeek.ordinal
        val newGregorianDate = gDate.plusDays(daysToAdd.toLong())
        return newGregorianDate.toAppDate()
    }


    override fun daysInMonth(year: Int, month: Int): Int {
        val days = YearMonth.of(year, month).lengthOfMonth()
        return days
    }

    override fun isLeapYear(year: Int): Boolean {
        return Year.of(year).isLeap
    }

    override fun monthName(month: Int, locale: Locale): String {
        return Month.of(month).getDisplayName(TextStyle.FULL, locale)
    }

    override fun formatDate(date: AppDate, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(locale)
        return date.toLocalDateTime().format(formatter)
    }

    override fun formatDateTime(dateTime: AppDateTime, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy , HH:mm").withLocale(locale)
        return dateTime.toLocalDateTime().format(formatter)
    }

    override fun formatTime(dateTime: AppDateTime, locale: Locale): String {
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy , HH:mm").withLocale(locale)
        return dateTime.toLocalDateTime().format(formatter)
    }

    override fun getDaysBetween(start: AppDate, end: AppDate): Long {
        val daysBetween = ChronoUnit.DAYS.between(
            start.toLocalDateTime().toLocalDate(),
            end.toLocalDateTime().toLocalDate()
        )
        return daysBetween
    }

    //Mappers
    private fun LocalDateTime.toAppDate(): AppDate {
        return AppDate(day = dayOfMonth, month = monthValue, year = year)
    }

    private fun LocalDateTime.toAppDateTime(): AppDateTime {
        return AppDateTime(
            date = AppDate(day = dayOfMonth, month = monthValue, year = year),
            hour = hour,
            minute = minute
        )
    }


    private fun AppDate.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.of(year, month, day, 0, 0)
    }

    private fun AppDateTime.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.of(date.year, date.month, date.day, hour, minute)
    }
}