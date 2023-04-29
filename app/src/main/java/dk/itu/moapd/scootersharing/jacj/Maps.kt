package dk.itu.moapd.scootersharing.jacj

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import dk.itu.moapd.scootersharing.jacj.models.Scooter
import dk.itu.moapd.scootersharing.jacj.services.Location.LocationService


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermissions() {
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )
    if (multiplePermissionsState.allPermissionsGranted) {
        // If all permissions are granted, then show screen with the feature enabled
        TestScreenMap()
    } else {
        SideEffect {
            multiplePermissionsState.run { launchMultiplePermissionRequest() }
        }
        Column (modifier = Modifier.fillMaxSize()) {
            Text(
                getTextToShowGivenPermissions(
                    multiplePermissionsState.revokedPermissions,
                    multiplePermissionsState.shouldShowRationale
                )
            )
        }
    }
}

@Composable
fun TestScreenMap(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .wrapContentWidth(Alignment.CenterHorizontally),
    viewModel: MainViewModel = viewModel(),
) {
    when (val result = viewModel.response.value) {
        is DataState.Loading -> {

                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

        }
        is DataState.Success -> {

                MapsScreenThis(result.data)

        }
        is DataState.Failure -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = result.message)
            }
        }
        else -> {
            Box(
                modifier = modifier.fillMaxSize(),
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
fun MapsScreenThis(scooters: MutableList<Scooter>) {
    //TODO: make the following a data class
    var isVisible by rememberSaveable { mutableStateOf(false) }
    var rideStarted by rememberSaveable { mutableStateOf(false) }
    var rideButtonText by rememberSaveable { mutableStateOf("Start Ride") }
    var title by rememberSaveable { mutableStateOf("") }
    var subtitle by rememberSaveable { mutableStateOf("") }

    val defaultLocation = LatLng(55.6596, 12.5910)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 16f)
    }
    Box {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            scooters.forEach { scooter ->
                Marker(
                    state = MarkerState(
                        position = LatLng(
                            scooter.coords!!.lat!!,
                            scooter.coords!!.long!!
                        ), //TODO: remove !!
                    ),
                    icon = getBitmapDescriptor(
                        R.drawable.map_scooter_marker,
                        LocalContext.current
                    ),
                    tag = 0,
                    onClick = {
                        isVisible = true
                        title = scooter.name.toString()
                        subtitle = scooter.location.toString()
                        false
                    }
                )
            }
            var testList: MutableList<LatLng> = ArrayList()
            testList.add(LatLng(55.6612727,12.6000592))
            testList.add(LatLng(55.6612725,12.6001239))
            testList.add(LatLng(55.6612754,12.6001351))
            testList.add(LatLng(55.6612731,12.6001029))
            Polyline(
                points = testList,
                color = Color(0xFF66BB6A),
                jointType = JointType.BEVEL,
                width = 20f
            )
        }
        if (isVisible) {
            Card(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp, 16.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp, 16.dp)
                            .wrapContentHeight()
                    ) {
                        Text(text = title)
                        Text(text = subtitle)
                    }
                    var context = LocalContext.current
                    Button(onClick = {
                        if(rideStarted) {
                            Intent(context, LocationService::class.java).apply {
                                action = LocationService.ACTION_STOP
                                context.startService(this)
                            }
                            rideButtonText = "Start Ride"
                        }
                        else{
                            Intent(context, LocationService::class.java).apply {
                                action = LocationService.ACTION_START
                                context.startService(this)
                            }
                            rideButtonText = "Stop Ride"
                        }
                        rideStarted = !rideStarted
                    }) {
                        Text(text = rideButtonText)
                    }
                }
            }
        }
    }
}

//TODO: BASED ON https://gist.github.com/Ozius/1ef2151908c701854736
private fun getBitmapDescriptor(id: Int, context: Context): BitmapDescriptor? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val vectorDrawable = AppCompatResources.getDrawable(
            context,
            id
        ) as VectorDrawable
        val h = vectorDrawable.intrinsicHeight
        val w = vectorDrawable.intrinsicWidth
        vectorDrawable.setBounds(0, 0, w, h)
        val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bm)
        vectorDrawable.draw(canvas)
        BitmapDescriptorFactory.fromBitmap(bm)
    } else {
        BitmapDescriptorFactory.fromResource(id)
    }
}