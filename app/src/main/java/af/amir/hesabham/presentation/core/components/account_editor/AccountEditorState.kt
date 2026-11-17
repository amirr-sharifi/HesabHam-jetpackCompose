package af.amir.hesabham.presentation.core.components.account_editor

import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.util.generateStringId
import java.math.BigDecimal

data class AccountEditorState(
    val account: Account = Account(
        AccountId(generateStringId()), "", false, AccountType.CASH,
        Money(BigDecimal.ZERO)
    ),
    val balance: BigDecimal = BigDecimal.ZERO,
    val isUpdating: Boolean = false,
)
