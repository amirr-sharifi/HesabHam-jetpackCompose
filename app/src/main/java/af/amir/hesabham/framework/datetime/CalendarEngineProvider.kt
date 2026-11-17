package af.amir.hesabham.framework.datetime

import af.amir.hesabham.domain.model.settings.AppCalendar

interface CalendarEngineProvider {
    fun get(
        calendar: AppCalendar,
    ): CalendarEngine
}