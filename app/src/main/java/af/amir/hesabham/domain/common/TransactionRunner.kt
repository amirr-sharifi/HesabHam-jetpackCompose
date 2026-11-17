package af.amir.hesabham.domain.common

interface TransactionRunner {

    suspend fun <T> run(block: suspend () -> T): T
}