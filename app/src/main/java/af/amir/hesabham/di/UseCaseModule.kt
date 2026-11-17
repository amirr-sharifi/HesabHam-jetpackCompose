package af.amir.hesabham.di

import af.amir.hesabham.domain.usecase.account.AccountUseCase
import af.amir.hesabham.domain.usecase.account.ChangeAccountArchiveStatusUseCase
import af.amir.hesabham.domain.usecase.account.CreateAccountUseCase
import af.amir.hesabham.domain.usecase.account.GetAccountUseCase
import af.amir.hesabham.domain.usecase.account.ObserveTotalBalanceUseCase
import af.amir.hesabham.domain.usecase.account.UpdateAccountBalanceUseCase
import af.amir.hesabham.domain.usecase.account.UpdateAccountUseCase
import af.amir.hesabham.domain.usecase.category.AddCategoryUseCase
import af.amir.hesabham.domain.usecase.category.CategoryUseCase
import af.amir.hesabham.domain.usecase.category.ChangeCategoryArchiveStatusUseCase
import af.amir.hesabham.domain.usecase.category.ObserveCategoriesUseCase
import af.amir.hesabham.domain.usecase.settings.ObserveAppSettingUseCase
import af.amir.hesabham.domain.usecase.settings.SetAppCalendarUseCase
import af.amir.hesabham.domain.usecase.settings.SetAppCurrencyUseCase
import af.amir.hesabham.domain.usecase.settings.SetAppLocaleUseCase
import af.amir.hesabham.domain.usecase.settings.SettingsUseCase
import af.amir.hesabham.domain.usecase.settings.SetupSettingsUseCase
import af.amir.hesabham.domain.usecase.settings.ToggleAppDarkModeUseCase
import af.amir.hesabham.domain.usecase.transaction.DeleteTransactionUseCase
import af.amir.hesabham.domain.usecase.transaction.GetInitialBalanceUseCase
import af.amir.hesabham.domain.usecase.transaction.GetTransactionUseCase
import af.amir.hesabham.domain.usecase.transaction.GetTransferUseCase
import af.amir.hesabham.domain.usecase.transaction.ObserveCategoryExpenseSummary
import af.amir.hesabham.domain.usecase.transaction.ObserveMonthlySummaryUseCase
import af.amir.hesabham.domain.usecase.transaction.ObserveTopTransactionsUseCase
import af.amir.hesabham.domain.usecase.transaction.ObserveTransactionWithRelationsUseCase
import af.amir.hesabham.domain.usecase.transaction.RecordOpeningBalanceTransactionUseCase
import af.amir.hesabham.domain.usecase.transaction.RecordTransactionUseCase
import af.amir.hesabham.domain.usecase.transaction.RecordTransferTransactionUseCase
import af.amir.hesabham.domain.usecase.transaction.TransactionUseCase
import af.amir.hesabham.domain.usecase.transaction.UpdateTransactionUseCase
import af.amir.hesabham.domain.usecase.transaction.UpdateTransferUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule = module {

    //Transaction
    factoryOf(::RecordOpeningBalanceTransactionUseCase)
    factoryOf(::RecordTransactionUseCase)
    factoryOf(::RecordTransferTransactionUseCase)
    factoryOf(::UpdateTransactionUseCase)
    factoryOf(::UpdateTransferUseCase)
    factoryOf(::DeleteTransactionUseCase)
    factoryOf(::GetInitialBalanceUseCase)
    factoryOf(::ObserveMonthlySummaryUseCase)
    factoryOf(::ObserveTransactionWithRelationsUseCase)
    factoryOf(::ObserveCategoryExpenseSummary)
    factoryOf(::GetTransferUseCase)
    factoryOf(::GetTransactionUseCase)
    factoryOf(::ObserveTopTransactionsUseCase)
    singleOf(::TransactionUseCase)

    //Account
    factoryOf(::GetAccountUseCase)
    factoryOf(::CreateAccountUseCase)
    factoryOf(::UpdateAccountUseCase)
    factoryOf(::ObserveTotalBalanceUseCase)
    factoryOf(::ChangeAccountArchiveStatusUseCase)
    singleOf(::AccountUseCase)

    factoryOf(::UpdateAccountBalanceUseCase)


    //settings
    singleOf(::ObserveAppSettingUseCase)
    factoryOf(::SetupSettingsUseCase)
    factoryOf(::SetAppLocaleUseCase)
    factoryOf(::SetAppCalendarUseCase)
    factoryOf(::SetAppCurrencyUseCase)
    factoryOf(::ToggleAppDarkModeUseCase)
    singleOf(::SettingsUseCase)




    //Category
    factoryOf(::AddCategoryUseCase)
    factoryOf(::ObserveCategoriesUseCase)
    factoryOf(::ChangeCategoryArchiveStatusUseCase)
    singleOf(::CategoryUseCase)
}