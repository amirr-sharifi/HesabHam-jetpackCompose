package af.amir.hesabham

import af.amir.hesabham.data.db.dao.AccountDao
import af.amir.hesabham.data.db.dao.TransactionDao
import af.amir.hesabham.data.db.entity.AccountEntity
import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.domain.usecase.settings.SettingsUseCase
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppConfigViewModel(
    settings: SettingsUseCase,
) : ViewModel() {



    val state: StateFlow<AppConfigState> =
        settings.observe().map { AppConfigState(isLoading = false, setting = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppConfigState())

}