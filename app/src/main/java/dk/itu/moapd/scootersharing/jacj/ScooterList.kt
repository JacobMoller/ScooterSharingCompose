package dk.itu.moapd.scootersharing.jacj

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.jacj.models.Coords
import dk.itu.moapd.scootersharing.jacj.models.Scooter
import dk.itu.moapd.scootersharing.jacj.ui.theme.ScooterSharingTheme

@Composable
fun ScooterListScreen() {
    var auth = FirebaseAuth.getInstance()
    val viewModel: MainViewModel = viewModel()
    when (val result = viewModel.response.value) {
        is DataState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
        }
        is DataState.Success -> {
            ShowLazyList(result.data)
        }
        is DataState.Failure -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = result.message)
            }
        }
        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error Fetching Data",
                    fontSize = MaterialTheme.typography.headlineMedium.fontSize
                )
            }
        }
    }
}

@Preview
@Composable
private fun ShowLazyListPreview() {
    val dummyScooters: List<Scooter> = listOf(
        Scooter("CPH001", "?", Coords(123.456, 123.456)),
        Scooter("CPH002", "2", Coords(123.456, 123.456))
    )

    LazyColumn {
        items(dummyScooters.toList()) { scooter ->
            ScooterCard(scooter)
        }
    }
}

@Composable
private fun ShowLazyList(scooters: MutableList<Scooter>) {
    LazyColumn {
        items(scooters.toList()) { scooter ->
            ScooterCard(scooter)
        }
    }
}

@Composable
private fun ScooterCard(
    scooter: Scooter,
    modifier: Modifier = Modifier
) {

        Card(
            modifier = modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(15.dp)
        ) {
            val defaultImage =
                painterResource(id = R.drawable.baseline_image_24)
            Column(modifier = modifier) {
                Image(
                    painter = rememberAsyncImagePainter(
                        scooter.image,
                        placeholder = defaultImage,
                        fallback = defaultImage,
                        error = defaultImage,
                    ),
                    contentDescription = "Scooter-image", //TODO: make this a string resource
                    contentScale = ContentScale.Fit,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = modifier,
                        text = scooter.name.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        modifier = modifier,
                        text = scooter.location.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(text = "Reserve",
                        modifier = modifier
                            .padding(0.dp, 8.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                            ) {
                                //TODO: onclick action here
                            },
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
}