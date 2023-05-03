package dk.itu.moapd.scootersharing.jacj

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun RideSummary(navController: NavHostController) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Ride summary")
        Text(text = "Minutes")
        Text(text = "x")
        Text(text = "km/h")
        Text(text = "x")
        Text(text = "distance")
        Text(text = "x")
        Button(onClick = {
            navController.navigate("HomePage/0/0/")
            //TODO: Replace with navigation to pastrides
        }) {
            Text(text = "Close")
        }
    }
}