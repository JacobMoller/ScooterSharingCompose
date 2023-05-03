package dk.itu.moapd.scootersharing.jacj.core.presentation.components.user_scooter_image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.storage.FirebaseStorage
import dk.itu.moapd.scootersharing.jacj.R
import dk.itu.moapd.scootersharing.jacj.core.presentation.components.loadNetworkImage

@Composable
fun UserScooterImage(storage: FirebaseStorage, scooterName: String) {
    val defaultImage =
        painterResource(id = R.drawable.baseline_image_24)
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(LocalContext.current)
                .data(loadNetworkImage(scooterName,storage).value)
                .crossfade(true)
                .placeholder(R.drawable.baseline_image_24)
                .build(),
            placeholder = defaultImage,
            fallback = defaultImage,
            error = defaultImage,
        ),
        contentDescription = "Scooter-image", //TODO: make this a string resource
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    )
}