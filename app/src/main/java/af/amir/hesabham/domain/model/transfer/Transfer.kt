package af.amir.hesabham.domain.model.transfer

import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.account.AccountId
import org.threeten.bp.Instant

data class Transfer(
    val id : TransferId,
    val fromAccount : AccountId,
    val toAccount : AccountId,
    val amount : Money,
    val date :Instant,
    val note : String?
)
