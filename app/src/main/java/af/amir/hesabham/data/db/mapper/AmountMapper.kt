package af.amir.hesabham.data.db.mapper

import af.amir.hesabham.domain.model.Money
import android.util.Log
import java.math.BigDecimal
import java.math.RoundingMode


private const val SCALE = 6
private val MULTIPLIER = BigDecimal.TEN.pow(6)

fun Money.toLong(): Long {
    return amount
        .multiply(MULTIPLIER)
        .setScale(SCALE, RoundingMode.UNNECESSARY)
        .longValueExact()
}

fun Long.toMoney(): Money {
    return Money(
        amount = BigDecimal.valueOf(this).divide(MULTIPLIER, SCALE, RoundingMode.UNNECESSARY).stripTrailingZeros()
    )
}