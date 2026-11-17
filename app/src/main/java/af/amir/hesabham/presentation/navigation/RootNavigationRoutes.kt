package af.amir.hesabham.presentation.navigation

const val TRANSACTION_ID = "Transaction_Id"

sealed class RootNavigationRoutes(val route: String) {
    object Root : RootNavigationRoutes("root")
    object Onboarding : RootNavigationRoutes("onboarding")
    object AddTransaction : RootNavigationRoutes("addTransaction?transactionId={$TRANSACTION_ID}") {
        fun passTransactionId(id: String?): String {

            return if (id == null)
                "addTransaction" else
                "addTransaction?transactionId=$id"
        }
    }

    object HesabHam : RootNavigationRoutes("hesabHam")
    object Accounts : RootNavigationRoutes("accounts")

}