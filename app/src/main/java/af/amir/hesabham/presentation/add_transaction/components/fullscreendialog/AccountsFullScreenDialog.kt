package af.amir.hesabham.presentation.add_transaction.components.fullscreendialog

import af.amir.hesabham.R
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.domain.model.account.AccountType
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeRegistry
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AccountsFullScreenDialog(
    accounts: List<Account>,
    onDismissRequest: () -> Unit,
    onAccountSelected: (account: Account) -> Unit,
    onAddAccount: (type: AccountType) -> Unit,
    onEditAccount: (Account) -> Unit,
    onArchiveAccount: (Account) -> Unit,
    modifier: Modifier = Modifier,
) {


    val expandedHeaderIds = remember { mutableStateListOf<String>() }

    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)


    BackHandler {
        onDismissRequest()
    }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
    ) {

        Text(
            resources.getString(R.string.account_dialog_subtitle),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(16.dp))

        val headers = AccountTypeRegistry.items

        LazyColumn(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),

        ) {
            items(headers) { header ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FullScreenDialogItem(
                        title = header.text.asString(),
                        icon = header.icon,
                        showDropDown = false,
                        iconColor = MaterialTheme.colorScheme.primary,
                        onEdit = {},
                        onDelete = {},
                        onItemClick = {
                            val id = header.type.name
                            if (expandedHeaderIds.contains(id)) expandedHeaderIds -= id else expandedHeaderIds += id
                        }
                    )
                    if (expandedHeaderIds.contains(header.type.name)) {
                        val children = accounts.filter { it.type == header.type }
                        children.forEach { account ->
                            FullScreenDialogItem(
                                title = account.name,
                                icon = null,
                                showDropDown = true,
                                iconColor = MaterialTheme.colorScheme.primary,
                                onEdit = { onEditAccount(account) },
                                onDelete = { onArchiveAccount(account) },
                                onItemClick = {
                                    onAccountSelected(account)
                                    onDismissRequest()
                                },
                                modifier = Modifier
                                    .height(48.dp)
                                    .padding(start = 24.dp)
                            )
                        }
                        FullScreenDialogAddItem(Modifier.padding(start = 24.dp)) {
                            onAddAccount(
                                header.type
                            )
                        }
                    }
                }
            }
        }


    }
}


