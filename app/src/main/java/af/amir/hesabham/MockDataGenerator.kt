package af.amir.hesabham

import af.amir.hesabham.domain.common.TransactionRunner
import af.amir.hesabham.domain.model.Money
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountId
import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.domain.model.category.CategoryId
import af.amir.hesabham.domain.model.transaction.Transaction
import af.amir.hesabham.domain.model.transaction.TransactionId
import af.amir.hesabham.domain.model.transaction.TransactionType
import af.amir.hesabham.domain.model.transfer.Transfer
import af.amir.hesabham.domain.model.transfer.TransferId
import af.amir.hesabham.domain.repo.AccountRepository
import af.amir.hesabham.domain.repo.CategoryRepository
import af.amir.hesabham.domain.repo.TransactionRepository
import af.amir.hesabham.domain.util.SystemCategoryIds
import kotlinx.coroutines.flow.first
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import java.math.BigDecimal
import kotlin.random.Random

/**
 * Creates a coherent, repeatable USD demo dataset for screenshots and manual QA.
 * It is intentionally invoked manually and never runs automatically on app startup.
 */
class MockDataGenerator(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRunner: TransactionRunner,
) {

    suspend fun generate(): MockDataGenerationResult {
        val existingAccounts = accountRepository.getAll()
        if (existingAccounts.any { it.id.id.startsWith(MOCK_ID_PREFIX) }) {
            return MockDataGenerationResult.AlreadyGenerated
        }

        categoryRepository.getCategories().first { items ->
            REQUIRED_CATEGORY_IDS.all { id -> items.any { it.categoryId.id == id } }
        }

        val today = LocalDate.now()
        val firstMonth = today.withDayOfMonth(1).minusMonths(MONTHS_TO_GENERATE - 1L)
        val openingDate = firstMonth.minusDays(1)

        val checking = DemoAccount(
            id = AccountId("${MOCK_ID_PREFIX}checking"),
            name = "Checking Account",
            type = AccountType.BANK,
            openingBalance = dollars("4,500.00"),
        )
        val savings = DemoAccount(
            id = AccountId("${MOCK_ID_PREFIX}savings"),
            name = "Savings Account",
            type = AccountType.SAVING,
            openingBalance = dollars("12,000.00"),
        )
        val cash = DemoAccount(
            id = AccountId("${MOCK_ID_PREFIX}cash"),
            name = "Cash Wallet",
            type = AccountType.CASH,
            openingBalance = dollars("250.00"),
        )
        val accounts = listOf(checking, savings, cash)
        val balances = accounts.associate { it.id to it.openingBalance }.toMutableMap()

        val operations = createOperations(firstMonth, today, checking, savings, cash)
            .sortedBy { it.date }

        var transactionCount = accounts.size // One opening-balance transaction per account.
        var transferCount = 0
        var transactionNumber = 0
        var transferNumber = 0

        transactionRunner.run {
            accounts.forEach { demoAccount ->
                accountRepository.createAccount(
                    Account(
                        id = demoAccount.id,
                        name = demoAccount.name,
                        isArchive = false,
                        type = demoAccount.type,
                        balance = Money(demoAccount.openingBalance),
                    )
                )

                transactionRepository.insertTransaction(
                    Transaction(
                        id = TransactionId("${MOCK_ID_PREFIX}opening_${demoAccount.id.id}"),
                        type = TransactionType.INITIAL_BALANCE,
                        accountId = demoAccount.id,
                        amount = Money(demoAccount.openingBalance),
                        categoryId = CategoryId(SystemCategoryIds.INITIAL_CATEGORY_ID),
                        transferId = null,
                        date = openingDate.atNoon(),
                        note = "Opening balance",
                    )
                )
            }

            operations.forEach { operation ->
                when (operation) {
                    is DemoOperation.TransactionEntry -> {
                        transactionNumber++
                        val type = operation.type
                        transactionRepository.insertTransaction(
                            Transaction(
                                id = TransactionId("${MOCK_ID_PREFIX}transaction_$transactionNumber"),
                                type = type,
                                accountId = operation.accountId,
                                amount = Money(operation.amount),
                                categoryId = CategoryId(operation.categoryId),
                                transferId = null,
                                date = operation.date.atNoon(),
                                note = operation.note,
                            )
                        )

                        val signedAmount = if (type == TransactionType.INCOME) {
                            operation.amount
                        } else {
                            operation.amount.negate()
                        }
                        balances[operation.accountId] = requireNotNull(balances[operation.accountId])
                            .add(signedAmount)
                        transactionCount++
                    }

                    is DemoOperation.TransferEntry -> {
                        transferNumber++
                        val transferId = TransferId("${MOCK_ID_PREFIX}transfer_$transferNumber")
                        val amount = Money(operation.amount)
                        val date = operation.date.atNoon()

                        transactionRepository.insertTransaction(
                            Transaction(
                                id = TransactionId("${MOCK_ID_PREFIX}transfer_out_$transferNumber"),
                                type = TransactionType.TRANSFER_OUT,
                                accountId = operation.fromAccount,
                                amount = amount,
                                categoryId = null,
                                transferId = transferId,
                                date = date,
                                note = operation.note,
                            )
                        )
                        transactionRepository.insertTransaction(
                            Transaction(
                                id = TransactionId("${MOCK_ID_PREFIX}transfer_in_$transferNumber"),
                                type = TransactionType.TRANSFER_IN,
                                accountId = operation.toAccount,
                                amount = amount,
                                categoryId = null,
                                transferId = transferId,
                                date = date,
                                note = operation.note,
                            )
                        )
                        transactionRepository.insertTransfer(
                            Transfer(
                                id = transferId,
                                fromAccount = operation.fromAccount,
                                toAccount = operation.toAccount,
                                amount = amount,
                                date = date,
                                note = operation.note,
                            )
                        )

                        balances[operation.fromAccount] = requireNotNull(balances[operation.fromAccount])
                            .subtract(operation.amount)
                        balances[operation.toAccount] = requireNotNull(balances[operation.toAccount])
                            .add(operation.amount)
                        transactionCount += 2
                        transferCount++
                    }
                }
            }

            accounts.forEach { demoAccount ->
                val finalBalance = requireNotNull(balances[demoAccount.id])
                accountRepository.updateAccount(
                    Account(
                        id = demoAccount.id,
                        name = demoAccount.name,
                        isArchive = false,
                        type = demoAccount.type,
                        balance = Money(finalBalance),
                    )
                )
            }
        }

        return MockDataGenerationResult.Created(
            accountCount = accounts.size,
            transactionCount = transactionCount,
            transferCount = transferCount,
        )
    }

    private fun createOperations(
        firstMonth: LocalDate,
        today: LocalDate,
        checking: DemoAccount,
        savings: DemoAccount,
        cash: DemoAccount,
    ): List<DemoOperation> {
        val random = Random(DATA_SEED)
        val result = mutableListOf<DemoOperation>()

        fun addExpense(
            date: LocalDate,
            account: DemoAccount,
            categoryId: String,
            amount: BigDecimal,
            note: String,
        ) {
            if (!date.isAfter(today)) {
                result += DemoOperation.TransactionEntry(
                    date, TransactionType.EXPENSE, account.id, categoryId, amount, note
                )
            }
        }

        fun addIncome(
            date: LocalDate,
            categoryId: String,
            amount: BigDecimal,
            note: String,
        ) {
            if (!date.isAfter(today)) {
                result += DemoOperation.TransactionEntry(
                    date, TransactionType.INCOME, checking.id, categoryId, amount, note
                )
            }
        }

        fun addTransfer(
            date: LocalDate,
            from: DemoAccount,
            to: DemoAccount,
            amount: BigDecimal,
            note: String,
        ) {
            if (!date.isAfter(today)) {
                result += DemoOperation.TransferEntry(date, from.id, to.id, amount, note)
            }
        }

        var month = firstMonth
        while (!month.isAfter(today)) {
            addIncome(month.withDayOfMonth(1), "income_salary_monthly", dollars("4,800.00"), "Monthly salary")
            addIncome(month.withDayOfMonth(18), "income_business_services", randomDollars(random, 450, 750), "Freelance project")

            addExpense(month.withDayOfMonth(2), checking, "expense_home_rent", dollars("1,650.00"), "Monthly rent")
            addExpense(month.withDayOfMonth(5), checking, "expense_home_electricity", randomDollars(random, 85, 145), "Electricity bill")
            addExpense(month.withDayOfMonth(8), checking, "expense_home_water", randomDollars(random, 28, 48), "Water bill")
            addExpense(month.withDayOfMonth(10), checking, "expense_home_internet", dollars("65.00"), "Internet bill")
            addExpense(month.withDayOfMonth(14), checking, "expense_subscriptions_streaming", dollars("15.99"), "Streaming subscription")
            addExpense(month.withDayOfMonth(20), checking, "expense_subscriptions_software", dollars("12.99"), "Software subscription")
            addExpense(month.withDayOfMonth(23), checking, "expense_shopping_clothes", randomDollars(random, 45, 130), "Clothing and personal items")
            addExpense(month.withDayOfMonth(26), checking, "expense_health_pharmacy", randomDollars(random, 12, 38), "Pharmacy")

            addTransfer(month.withDayOfMonth(4), checking, savings, dollars("500.00"), "Monthly savings")
            addTransfer(month.withDayOfMonth(6), checking, cash, dollars("120.00"), "Cash withdrawal")

            month = month.plusMonths(1)
        }

        var week = 0L
        var date = firstMonth.plusDays(3)
        while (!date.isAfter(today)) {
            addExpense(
                date.plusDays(0), checking, "expense_food_groceries",
                randomDollars(random, 78, 142), "Groceries"
            )
            addExpense(
                date.plusDays(2), cash, "expense_food_cafe",
                randomDollars(random, 5, 11), "Coffee"
            )
            addExpense(
                date.plusDays(4), cash, "expense_food_restaurant",
                randomDollars(random, 18, 42), "Dinner"
            )

            if (week % 2L == 0L) {
                addExpense(
                    date.plusDays(5), checking, "expense_transport_fuel",
                    randomDollars(random, 38, 62), "Fuel"
                )
            } else {
                addExpense(
                    date.plusDays(5), checking, "expense_transport_public",
                    randomDollars(random, 12, 24), "Public transportation"
                )
            }

            date = date.plus(7, ChronoUnit.DAYS)
            week++
        }

        return result
    }

    private fun LocalDate.atNoon() = atTime(12, 0).atZone(ZoneId.systemDefault()).toInstant()

    private fun randomDollars(random: Random, minDollars: Int, maxDollars: Int): BigDecimal {
        val cents = random.nextInt(minDollars * 100, maxDollars * 100 + 1)
        return BigDecimal.valueOf(cents.toLong(), 2)
    }

    private fun dollars(value: String): BigDecimal = BigDecimal(value.replace(",", ""))

    private data class DemoAccount(
        val id: AccountId,
        val name: String,
        val type: AccountType,
        val openingBalance: BigDecimal,
    )

    private sealed interface DemoOperation {
        val date: LocalDate

        data class TransactionEntry(
            override val date: LocalDate,
            val type: TransactionType,
            val accountId: AccountId,
            val categoryId: String,
            val amount: BigDecimal,
            val note: String,
        ) : DemoOperation

        data class TransferEntry(
            override val date: LocalDate,
            val fromAccount: AccountId,
            val toAccount: AccountId,
            val amount: BigDecimal,
            val note: String,
        ) : DemoOperation
    }

    companion object {
        private const val MOCK_ID_PREFIX = "mock_"
        private const val DATA_SEED = 20261116
        private const val MONTHS_TO_GENERATE = 3L

        private val REQUIRED_CATEGORY_IDS = setOf(
            "income_salary_monthly",
            "income_business_services",
            "expense_home_rent",
            "expense_home_electricity",
            "expense_home_water",
            "expense_home_internet",
            "expense_subscriptions_streaming",
            "expense_subscriptions_software",
            "expense_shopping_clothes",
            "expense_health_pharmacy",
            "expense_food_groceries",
            "expense_food_cafe",
            "expense_food_restaurant",
            "expense_transport_fuel",
            "expense_transport_public",
        )
    }
}

sealed interface MockDataGenerationResult {
    data object AlreadyGenerated : MockDataGenerationResult

    data class Created(
        val accountCount: Int,
        val transactionCount: Int,
        val transferCount: Int,
    ) : MockDataGenerationResult
}
