package dk.itu.moapd.scootersharing.jacj.feature_scooters_list.presentation.scooter_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.jacj.feature_scooters_list.presentation.util.DataStateScooter
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Coords
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Scooter
import dk.itu.moapd.scootersharing.jacj.core.presentation.components.MainViewModel
import dk.itu.moapd.scootersharing.jacj.core.presentation.components.user_scooter_image.UserScooterImage
import dk.itu.moapd.scootersharing.jacj.feature_location_service.domain.util.setAddress

@Composable
fun ScooterListScreen(navController: NavHostController) {
    val viewModel: MainViewModel = viewModel()
    when (val result = viewModel.response.value) {
        is DataStateScooter.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is DataStateScooter.Success -> {
            ShowLazyList(result.data, navController)
        }

        is DataStateScooter.Failure -> {
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
        Scooter(name = "CPH001", coords = Coords(123.456, 123.456)),
        Scooter(name = "CPH002", coords = Coords(123.456, 123.456))
    )

    LazyColumn {
        items(dummyScooters.toList()) { scooter ->
            ScooterCard(scooter, rememberNavController())
        }
    }
}

@Composable
private fun ShowLazyList(items: MutableList<Scooter>, navController: NavController) {
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Text(
                text = "Scooter-List",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 25.dp, bottom = 25.dp)
            )
        }
        items(items.toList()) { item ->
            ScooterCard(item, navController)
        }
    }
}

@Composable
private fun ScooterCard(
    inputScooter: Scooter,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scooter: Scooter = inputScooter
    val storage = Firebase.storage
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(modifier = modifier) {
            UserScooterImage(storage, scooter.name!!)
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
                    text = setAddress(LocalContext.current, scooter.coords).toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Show on map",
                    modifier = modifier
                        .padding(0.dp, 8.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(color = MaterialTheme.colorScheme.primary),
                        ) {
                            navController.navigate("HomePage/${scooter.coords?.lat}/${scooter.coords?.long}/")
                        },
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}