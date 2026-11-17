package af.amir.hesabham.di

import af.amir.hesabham.presentation.core.mapper.UiCategoryMapper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val presentationModule = module {
    factory { UiCategoryMapper(androidContext()) }
}