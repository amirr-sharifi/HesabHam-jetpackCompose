package af.amir.hesabham.presentation.add_transaction.components.fullscreendialog

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FullScreenDialogAddItem(
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit,
) {
    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

    val iconColor = Color.Gray

    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onItemClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            Modifier
                .size(48.dp)
                .background(iconColor.copy(alpha = 0.07f), MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Add, "", Modifier.size(32.dp), tint = iconColor)
        }

        Text(resources.getString(R.string.add), Modifier.weight(1f))
    }
}