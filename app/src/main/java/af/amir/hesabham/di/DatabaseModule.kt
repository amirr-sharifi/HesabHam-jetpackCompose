package af.amir.hesabham.di

import af.amir.hesabham.data.db.DatabaseSeeder
import af.amir.hesabham.data.db.ExpenseDatabase
import af.amir.hesabham.data.db.RoomTransactionRunner
import af.amir.hesabham.data.db.dao.AccountDao
import af.amir.hesabham.data.db.dao.CategoryDao
import af.amir.hesabham.data.db.dao.TransactionDao
import af.amir.hesabham.data.db.dao.TransferDao
import af.amir.hesabham.domain.common.TransactionRunner
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {

    single<ExpenseDatabase> {
        Room
            .databaseBuilder(
                androidContext(),
                ExpenseDatabase::class.java,
                "ExpenseDatabase"
            )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(connection: SQLiteConnection) {
                    CoroutineScope(Dispatchers.IO).launch {
                        get<DatabaseSeeder>().apply {
                            seedInitialCategory()
                            seedSystemCategories()
                        }
                    }
                }
            })
            .build()
    }

    factory<TransactionRunner> {RoomTransactionRunner(get<ExpenseDatabase>())  }

    single<CategoryDao> { get<ExpenseDatabase>().categoryDao }
    single<AccountDao> { get<ExpenseDatabase>().accountDao }
    single<TransactionDao> { get<ExpenseDatabase>().transactionDao }
    single<TransferDao> { get<ExpenseDatabase>().transferDao }

    single { DatabaseSeeder(androidContext(), get()) }
}