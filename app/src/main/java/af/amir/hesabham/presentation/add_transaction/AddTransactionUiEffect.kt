package af.amir.hesabham.presentation.add_transaction

import af.amir.hesabham.presentation.core.model.UiText


sealed interface AddTransactionUiEffect {
    object NavigateUp : AddTransactionUiEffect
    data class ShowToast(val message :UiText) : AddTransactionUiEffect
}