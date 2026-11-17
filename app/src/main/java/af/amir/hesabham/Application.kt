package af.amir.hesabham

import af.amir.hesabham.di.appModule
import af.amir.hesabham.di.calendarModule
import af.amir.hesabham.di.databaseModule
import af.amir.hesabham.di.presentationModule
import af.amir.hesabham.di.useCaseModule
import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        startKoin {
            androidContext(this@Application)
            modules(
                useCaseModule,
                databaseModule,
                calendarModule,
                presentationModule,
                appModule
            )
        }
    }
}