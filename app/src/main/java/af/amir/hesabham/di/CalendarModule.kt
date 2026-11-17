package af.amir.hesabham.di

import af.amir.hesabham.framework.datetime.CalendarEngineProvider
import af.amir.hesabham.framework.datetime.CalendarEngineProviderImpl
import af.amir.hesabham.framework.datetime.GregorianCalendarEngine
import af.amir.hesabham.framework.datetime.PersianCalendarEngine
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val calendarModule = module {

    singleOf(::PersianCalendarEngine)
    singleOf(::GregorianCalendarEngine)

    factoryOf(::CalendarEngineProviderImpl).bind<CalendarEngineProvider>()

}