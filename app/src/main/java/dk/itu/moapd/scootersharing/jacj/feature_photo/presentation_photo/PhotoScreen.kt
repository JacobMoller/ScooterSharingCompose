package dk.itu.moapd.scootersharing.jacj.feature_photo.presentation_photo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Scooter
import java.io.ByteArrayOutputStream


@Composable
fun PhotoScreen(navController: NavHostController, scooter: Scooter) {
    val context = LocalContext.current
    var hasCameraPermission by rememberSaveable {
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
    var shouldShowPhoto by rememberSaveable { mutableStateOf(false) }
    var bitmap by rememberSaveable {
        mutableStateOf(
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            )
        )
    }
    val resultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    bitmap = result.data?.extras?.get("data") as Bitmap
                    shouldShowPhoto = true

                    //Upload photo
                    val storage = Firebase.storage
                    val auth = FirebaseAuth.getInstance()
                    auth.currentUser?.let {
                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        val image = storage.reference.child("scooters/${scooter.name}.png")
                        uploadImageToBucket(data, image, navController)
                    }
                }
            }
        }

    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (hasCameraPermission) {
            Text(text = "Please park the scooter safely and take a photo of the parked scooter.")
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

private fun uploadImageToBucket(
    test: ByteArray,
    image: StorageReference,
    navController: NavHostController
) {
    image.putBytes(test).addOnSuccessListener {
        navController.navigate("RideSummary")
    }
}