package af.amir.hesabham.presentation.core.components

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun ArchiveConfirm(isForAccount: Boolean, onCancel: () -> Unit, onConfirm: () -> Unit) {
    val resources = rememberLocalizeResources(LocalAppLocale.current)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.error.copy(alpha = .08f))
            .padding(16.dp)
    ) {
        Text(
            if (isForAccount) resources.getString(R.string.account_archive_warning) else
                resources.getString(R.string.category_archive_warning),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = MaterialTheme.typography.labelMedium.fontSize
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            ) { Text(resources.getString(R.string.archive)) }
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f)
            ) { Text(resources.getString(R.string.cancel), color = Teal) }
        }
    }
}
