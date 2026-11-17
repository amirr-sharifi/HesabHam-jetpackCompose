package af.amir.hesabham.presentation.add_transaction.components

import af.amir.hesabham.R
import af.amir.hesabham.presentation.core.components.dialog.BottomAttachDialog
import af.amir.hesabham.presentation.core.config.setting.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

@Composable
fun CommentDialog(
    modifier: Modifier = Modifier,
    comment: String,
    onDismissDialog: () -> Unit,
    onDoneClick: (String) -> Unit,
) {
    var currentComment by rememberSaveable(comment) { mutableStateOf(comment) }
    val locale = LocalAppLocale.current
    val resources = rememberLocalizeResources(locale)
    val textColor = MaterialTheme.colorScheme.onSurface

    BottomAttachDialog(onDismiss = onDismissDialog) {
        Column(
            modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = currentComment,
                placeholder = {
                    Text(
                        resources.getString(R.string.comment_dialog_placeholder),
                        color = textColor.copy(alpha = 0.7f)
                    )
                },
                onValueChange = { currentComment = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    onDoneClick(currentComment)
                }),
                colors = TextFieldDefaults.colors(
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                    focusedTextColor = textColor,
                    disabledContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                )
            )

            TextButton(onClick = { onDoneClick(currentComment) }) {
                Text(text = resources.getString(R.string.Done))
            }
        }
    }
}