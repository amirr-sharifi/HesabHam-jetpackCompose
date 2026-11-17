package af.amir.hesabham.di

import af.amir.hesabham.AppConfigViewModel
import af.amir.hesabham.MockDataGenerator
import af.amir.hesabham.data.prefrence.SettingsDataManager
import af.amir.hesabham.data.repo.AccountRepositoryImpl
import af.amir.hesabham.data.repo.CategoryRepositoryImpl
import af.amir.hesabham.data.repo.DefaultCurrencyListProvider
import af.amir.hesabham.data.repo.SettingsRepositoryImpl
import af.amir.hesabham.data.repo.TransactionRepositoryImpl
import af.amir.hesabham.domain.repo.CategoryRepository
import af.amir.hesabham.domain.provider.CurrencyListProvider
import af.amir.hesabham.domain.repo.SettingsRepository
import af.amir.hesabham.domain.repo.AccountRepository
import af.amir.hesabham.domain.repo.TransactionRepository
import af.amir.hesabham.presentation.accounts.AccountsViewModel
import af.amir.hesabham.presentation.add_transaction.AddTransactionViewModel
import af.amir.hesabham.presentation.core.components.amountinput.AmountInputViewModel
import af.amir.hesabham.presentation.core.components.dialog.datepickerdialog.WheelDatePickerViewModel
import af.amir.hesabham.presentation.home.screens.dashboard.DashboardViewModel
import af.amir.hesabham.presentation.home.screens.reports.ReportsViewModel
import af.amir.hesabham.presentation.home.screens.settings.SettingsViewModel
import af.amir.hesabham.presentation.home.screens.transactions.TransactionsUiMapper
import af.amir.hesabham.presentation.home.screens.transactions.TransactionsViewModel
import af.amir.hesabham.presentation.onboarding.OnboardingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    single<CurrencyListProvider> { DefaultCurrencyListProvider(androidContext()) }
    single<SettingsRepository> { SettingsRepositoryImpl(SettingsDataManager(androidContext())) }
    singleOf(::CategoryRepositoryImpl) { bind<CategoryRepository>() }
    singleOf(::AccountRepositoryImpl) { bind<AccountRepository>() }
    singleOf(::TransactionRepositoryImpl) { bind<TransactionRepository>() }
    factoryOf(::MockDataGenerator)

    viewModelOf(::AppConfigViewModel)
    viewModelOf(::WheelDatePickerViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::AmountInputViewModel)
    viewModelOf(::AddTransactionViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::AccountsViewModel)
    viewModelOf(::ReportsViewModel)
    viewModelOf(::SettingsViewModel)
    viewModel { TransactionsViewModel(get(), get(), get(), TransactionsUiMapper(androidContext())) }

}