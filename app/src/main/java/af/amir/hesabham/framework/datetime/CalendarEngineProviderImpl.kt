package af.amir.hesabham.framework.datetime

import af.amir.hesabham.domain.model.settings.AppCalendar

class CalendarEngineProviderImpl(
    private val persian : PersianCalendarEngine,
    private val gregorian : GregorianCalendarEngine
) : CalendarEngineProvider {
    override fun get(calendar: AppCalendar): CalendarEngine {
        return when(calendar){
            AppCalendar.Gregorian -> gregorian
            AppCalendar.Jalali -> persian
        }
    }
}