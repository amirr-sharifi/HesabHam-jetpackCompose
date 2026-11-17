package af.amir.hesabham.presentation.core.components


import af.amir.hesabham.R
import af.amir.hesabham.domain.model.account.Account
import af.amir.hesabham.presentation.core.config.rememberFormattedAmount
import af.amir.hesabham.presentation.core.config.setting.LocalAppCurrency
import af.amir.hesabham.presentation.core.config.setting.LocalAppLocale
import af.amir.hesabham.presentation.core.config.setting.rememberLocalizeResources
import af.amir.hesabham.presentation.core.model.accounttype.AccountTypeRegistry
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

val Teal = Color(0xFF2A7A72)
private val TealDark = Color(0xFF1B5F57)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailBottomSheet(
    account: Account,
    onDismiss: () -> Unit,
    onAddTransaction: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        dragHandle = {
            Box(
                Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 44.dp, height = 5.dp)
                    .clip(CircleShape)
                    .background(Teal.copy(alpha = .25f))
            )
        }
    ) {
        var confirmDelete by remember { mutableStateOf(false) }

        LazyColumn(
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeaderCard(account) }
            item {
                ActionsRow(onAddTransaction, onEdit) { confirmDelete = true }

            }
            item {
                AnimatedVisibility(
                    visible = confirmDelete,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) { ArchiveConfirm(isForAccount = true,onCancel = { confirmDelete = false }, onConfirm = onDelete) }
            }
        }
    }

}

@Composable
private fun HeaderCard(account: Account) {
    val transition = rememberInfiniteTransition(label = "wave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "phase"
    )

    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)

    val accountType = AccountTypeRegistry.get(account)


    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.verticalGradient(listOf(Teal, TealDark)))
    ) {
        Canvas(Modifier.matchParentSize()) {
            fun wave(alpha: Float, amp: Float, shift: Float, base: Float) {
                val path = Path().apply {
                    moveTo(0f, size.height)
                    var x = 0f
                    while (x <= size.width) {
                        val y = size.height * base +
                                sin(x / size.width * 2f * Math.PI.toFloat() + phase + shift) * amp
                        lineTo(x, y); x += 8f
                    }
                    lineTo(size.width, size.height); close()
                }
                drawPath(path, Color.White.copy(alpha = alpha))
            }
            wave(.08f, 14.dp.toPx(), 0f, .72f)
            wave(.06f, 10.dp.toPx(), 2f, .82f)
        }

        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = .18f)),
                    contentAlignment = Alignment.Center
                ) { Icon(accountType.icon, null, tint = Color.White) }
                Spacer(Modifier.width(12.dp))
                Text(account.name, color = Color.White, fontSize = 20.sp)
            }
            Spacer(Modifier.height(18.dp))
            Text(
                resources.getString(R.string.account_balance),
                color = Color.White.copy(alpha = .75f),
                fontSize = 14.sp
            )
            Text(
                rememberFormattedAmount(
                    account.balance.amount.toPlainString(),
                    LocalAppLocale.current
                ),
                color = Color.White,
                fontSize = 38.sp,
                textAlign = TextAlign.Center
            )
            Text(
                LocalAppCurrency.current.symbol,
                color = Color.White.copy(alpha = .75f),
                fontSize = 16.sp
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}


@Composable
private fun ActionsRow(
    onAdd: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit,
) {
    val resources = rememberLocalizeResources(LocalAppLocale.current)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .padding(vertical = 16.dp),
    ) {
        ActionItem(
            resources.getString(R.string.add_transaction),
            Icons.Rounded.Add,
            MaterialTheme.colorScheme.primary,
            Modifier.weight(1f),
            onAdd
        )
        ActionItem(
            resources.getString(R.string.edit_account),
            Icons.Rounded.Edit,
            MaterialTheme.colorScheme.primary,
            Modifier.weight(1f),
            onEdit
        )
        ActionItem(
            resources.getString(R.string.archive),
            Icons.Rounded.DeleteOutline,
            MaterialTheme.colorScheme.error,
            Modifier.weight(1f),
            onDelete
        )
    }
}

@Composable
private fun ActionItem(
    label: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val source = remember { MutableInteractionSource() }
    val pressed by source.collectIsPressedAsState()
    val scale by animateFloatAsState(
        if (pressed) .88f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "press"
    )
    Column(
        modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(source, null, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(tint.copy(alpha = .1f)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = tint) }
        Spacer(Modifier.height(6.dp))
        Text(
            label,
            fontSize = MaterialTheme.typography.labelMedium.fontSize,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}




