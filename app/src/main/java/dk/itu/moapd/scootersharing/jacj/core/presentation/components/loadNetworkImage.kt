package dk.itu.moapd.scootersharing.jacj.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

@Composable
fun loadNetworkImage(
    url: String,
    imageRepository: FirebaseStorage
): State<String> {
    return produceState(initialValue = "", url, imageRepository) {
        val image = imageRepository.getReference("scooters/${url}.png").downloadUrl.await()
        value = image.toString()
    }
}