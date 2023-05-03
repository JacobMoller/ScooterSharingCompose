package dk.itu.moapd.scootersharing.jacj.feature_past_rides.presentation.past_rides

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.jacj.core.presentation.components.user_scooter_image.UserScooterImage
import dk.itu.moapd.scootersharing.jacj.feature_past_rides.domain.model.PastRide
import dk.itu.moapd.scootersharing.jacj.feature_past_rides.presentation.util.DataStatePastRide
import java.sql.Timestamp
import java.util.Date

@Composable
fun PastRidesScreen() {
    val viewModel: PastRidesViewModel = viewModel()
    when (val result = viewModel.response.value) {
        is DataStatePastRide.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is DataStatePastRide.Success -> {
            ShowLazyList(result.data)
        }

        is DataStatePastRide.Failure -> {
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

@Composable
private fun ShowLazyList(items: MutableList<PastRide>) {
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Text(
                text = "Past Rides",
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 25.dp, bottom = 25.dp)
            )
        }
        if (items.toList().isEmpty()) {
            item {
                Text(text = "No rides yet.")
            }
        } else {
            items(items.toList()) { item ->
                ScooterCard(item)
            }
        }
    }
}

@Composable
private fun ScooterCard(
    pastRide: PastRide,
    modifier: Modifier = Modifier
) {
    val storage = Firebase.storage
    Card(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(modifier = modifier) {
            UserScooterImage(storage, pastRide.scooter?.name!!)
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = modifier,
                    text = pastRide.scooter?.name.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    modifier = modifier,
                    text = getDateTime(pastRide.timestamp ?: 0),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

private fun getDateTime(timestamp: Long): String {
    val stamp = Timestamp(timestamp)
    val date = Date(stamp.time)
    return date.toString()
}