package dk.itu.moapd.scootersharing.jacj

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat


@Composable
fun PhotoPage() {
    var context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }
    var shouldShowPhoto by remember { mutableStateOf(false) }
    var _bitmap by remember { mutableStateOf(Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888)) }
    val resultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result?.data != null) {
                    _bitmap = result.data?.extras?.get("data") as Bitmap
                    shouldShowPhoto = true
                }
            }
        }

    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(20.dp)) {
        if(hasCameraPermission) {
            Text(text = "Test")
            if (shouldShowPhoto) {
                Image(bitmap = _bitmap.asImageBitmap(), contentDescription = "test")
            } else {
                Text(text = "Take a photo to show image here")
            }
            Button(onClick = {
                resultLauncher.launch(cameraIntent)
            }) {
                Text(text = "Click to open camera")
            }
        } else {
            Text(text = "Needs camera permissions")
        }
    }
}