package af.amir.hesabham.domain.provider

import af.amir.hesabham.domain.model.settings.AppCurrency

interface CurrencyListProvider {
    suspend fun getALl() : List<AppCurrency>
}