package dk.itu.moapd.scootersharing.jacj.feature_map.presentation.map.components.map_marker

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dk.itu.moapd.scootersharing.jacj.R


@Composable
fun StartRideDialog(onCancel: () -> Unit, confirmOnClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = stringResource(R.string.map_dialog_unlock_scooter))
        },
        text = {
            Text(stringResource(R.string.map_dialog_description), color = Color.Black)
        },
        confirmButton = {
            Button(onClick = confirmOnClick) {
                Text(stringResource(R.string.map_dialog_confirm))
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(stringResource(R.string.map_dialog_cancel))
            }
        }
    )
}