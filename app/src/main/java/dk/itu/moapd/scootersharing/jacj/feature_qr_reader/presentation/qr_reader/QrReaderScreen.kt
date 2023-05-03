package dk.itu.moapd.scootersharing.jacj

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Scooter
import dk.itu.moapd.scootersharing.jacj.feature_qr_reader.domain.util.QrCodeAnalyzer

@Composable
fun QrReaderScreen(navController: NavHostController, scooter: Scooter?) {
    Log.i("HEEEEE", "Now in QrCodeScanner")
    if(scooter == null){
        navController.navigate("HomePage/0/0/")
    }
    Log.i("HEEEEE", "Current scooter in QRSCANNER" + scooter.toJson().toString())
    Log.i("HEEEEE", "further in QrCodeScanner #1")
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    Log.d("HEEEE", "hasCameraPermission is currently: " + hasCameraPermission)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )
    Log.i("HEEEEE", "further in QrCodeScanner #2")
    LaunchedEffect(key1 = true) {
        launcher.launch(Manifest.permission.CAMERA)
    }
    Log.i("HEEEEE", "further in QrCodeScanner #3")
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if(hasCameraPermission) {
            val primary = MaterialTheme.colorScheme.primary
            val stroke = Stroke(width = 30f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(50f, 50f), 0f)
            )
            Text(
                text = "Scan the QR Code on the scooter",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 50.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(50.dp)
                    .aspectRatio(1f)
                    .drawBehind {
                        drawRoundRect(
                            color = primary,
                            style = stroke,
                            cornerRadius = CornerRadius(20.dp.toPx())
                        )
                    }
                    .clipToBounds()
            ) {
                Log.i("HEEEEE", "further in QrCodeScanner #4")
                AndroidView(
                    factory = { context ->
                        val previewView = PreviewView(context)
                        val preview = Preview.Builder().build()
                        val selector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()
                        preview.setSurfaceProvider(previewView.surfaceProvider)
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                        imageAnalysis.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            QrCodeAnalyzer { result ->
                                Log.d("HEEEE", "We have a result!")

                                if("CPH" + result == scooter?.name){
                                    Log.d("HEEEE", "We have a match!")
                                    navController.navigate("HomePage/" + scooter?.coords?.lat + "/" + scooter?.coords?.long + "/?QRScanned=" + scooter.toJson())
                                }
                                else {
                                    Log.i("HEEEE", "No result. Compared CPH" + result + " with " + scooter?.name)
                                }
                            }
                        )
                        try {
                            cameraProviderFuture.get().bindToLifecycle(
                                lifecycleOwner,
                                selector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        previewView
                    },
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(20.dp)),
                )
            }
        }
    }
}