package dk.itu.moapd.scootersharing.jacj.feature_map.presentation.map.map_screen

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.maps.android.compose.CameraMoveStartedReason
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import dk.itu.moapd.scootersharing.jacj.feature_scooters_list.presentation.util.DataStateScooter
import dk.itu.moapd.scootersharing.jacj.R
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Scooter
import dk.itu.moapd.scootersharing.jacj.core.presentation.components.MainViewModel
import dk.itu.moapd.scootersharing.jacj.core.presentation.components.user_scooter_image.UserScooterImage
import dk.itu.moapd.scootersharing.jacj.feature_location_service.domain.LocationService
import dk.itu.moapd.scootersharing.jacj.feature_location_service.domain.LocationService.Companion.locationTrace
import dk.itu.moapd.scootersharing.jacj.feature_location_service.domain.LocationService.Companion.price
import dk.itu.moapd.scootersharing.jacj.feature_location_service.domain.util.setAddress
import dk.itu.moapd.scootersharing.jacj.feature_map.domain.model.RideState
import dk.itu.moapd.scootersharing.jacj.feature_map.presentation.map.components.map_marker.MapMarker
import dk.itu.moapd.scootersharing.jacj.feature_map.presentation.map.components.start_ride_dialog.StartRideDialog
import dk.itu.moapd.scootersharing.jacj.feature_map.presentation.util.getTextToShowGivenPermissions
import dk.itu.moapd.scootersharing.jacj.feature_authentication.feature_past_rides.domain.model.PastRide
import dk.itu.moapd.scootersharing.jacj.toJson


var latInput: Double = 55.652407
var longInput: Double = 12.558773
var QrScanned : Scooter? = null
var defaultLocationAltered = false

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermissions(
    lat: String?,
    long: String?,
    inputNavController: NavHostController,
    QRScanned: Scooter?
) {
    val navController : NavController = inputNavController
    QrScanned = QRScanned
    if(lat != null && lat != "null" && lat != "0") {
        latInput = lat.toDouble()
        defaultLocationAltered = true
    }
    if(long != null && long != "null" && long != "0") {
        longInput = long.toDouble()
        defaultLocationAltered = true
    }
    val multiplePermissionsState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )
    if (multiplePermissionsState.allPermissionsGranted) {
        // If all permissions are granted, then show screen with the feature enabled
        LoadMapDataScreen(navController = navController)
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
fun LoadMapDataScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
) {
    when (val result = viewModel.response.value) {
        is DataStateScooter.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is DataStateScooter.Success -> MapsScreen(navController, result.data)
        is DataStateScooter.Failure -> {
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
fun MapsScreen(navController: NavController, scooters: MutableList<Scooter>) {
    val viewModel: MainViewModel = viewModel()

    //TODO: make the following a data class
    var isVisible by rememberSaveable { mutableStateOf(QrScanned != null) }
    var rideState by rememberSaveable { mutableStateOf(RideState.SCAN) }
    var title by rememberSaveable { mutableStateOf("") }
    var subtitle by rememberSaveable { mutableStateOf("") }

    var defaultLocation = LatLng(latInput, longInput)
    var zoomLevel = 12.25f

    if (QrScanned != null) {
        rideState = RideState.START
        title = QrScanned?.name.toString()
        subtitle = setAddress(context = LocalContext.current, QrScanned?.coords).toString()
        if (QrScanned?.coords?.lat != null && QrScanned?.coords?.long != null) {
            defaultLocation = LatLng(QrScanned?.coords?.lat!!, QrScanned?.coords?.long!!)
            defaultLocationAltered = true
        }
        viewModel.currentScooter.value = QrScanned
    }
    if (defaultLocationAltered) {
        zoomLevel = 16f
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, zoomLevel)
    }
    val context = LocalContext.current
    Box {
        val openDialog = rememberSaveable { mutableStateOf(false) }
        val mapColor = if (isSystemInDarkTheme()) MapStyleOptions.loadRawResourceStyle(
            context,
            R.raw.dark_map
        ) else null
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true, mapStyleOptions = mapColor),
            uiSettings = MapUiSettings(zoomControlsEnabled = false, mapToolbarEnabled = false)
        ) {
            if (cameraPositionState.cameraMoveStartedReason == CameraMoveStartedReason.GESTURE && rideState != RideState.STOP) {
                isVisible = false
            }
            scooters.forEach { scooter ->
                scooter.coords?.let {
                    MapMarker(coordinates = it, onClick = {
                        if (QrScanned != null && QrScanned?.name == scooter.name) {
                            rideState = RideState.START
                        }
                        if (QrScanned != null && QrScanned?.name != scooter.name) {
                            rideState = RideState.SCAN
                        }
                        isVisible = true
                        title = scooter.name.toString()
                        subtitle = setAddress(context = context, QrScanned?.coords).toString()
                        viewModel.currentScooter.value = scooter
                        false
                    })
                }
            }
        }
        if (isVisible) {
            Card(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp, 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp, 16.dp),
                ) {
                    if (rideState == RideState.START && openDialog.value) {
                        StartRideDialog(
                            onCancel = {
                                openDialog.value = false
                                rideState = RideState.SCAN
                            },
                            confirmOnClick = {
                                openDialog.value = true
                                Intent(context, LocationService::class.java).apply {
                                    action = LocationService.ACTION_START
                                    context.startService(this)
                                }
                                rideState = RideState.STOP
                            })
                    }
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = setAddress(
                            context,
                            viewModel.currentScooter.value?.coords
                        ).toString()
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .wrapContentHeight()
                                .weight(2f)
                                .padding(end = 20.dp)
                        ) {
                            if (rideState != RideState.STOP) {
                                UserScooterImage(
                                    viewModel.storage,
                                    viewModel.currentScooter.value?.name!!
                                )
                            }
                        }
                        Button(
                            onClick = {
                                when (rideState) {
                                    RideState.SCAN -> navController.navigate("QrCodeScannerPage/${viewModel.currentScooter.value.toJson()}/")
                                    RideState.START -> openDialog.value = true
                                    RideState.STOP -> {
                                        Intent(context, LocationService::class.java).apply {
                                            action = LocationService.ACTION_STOP
                                            context.startService(this)
                                        }
                                        if (locationTrace.size > 0) {
                                            val newLocation = locationTrace.last()
                                            val id =
                                                viewModel.currentScooter.value?.name?.replace(
                                                    "CPH",
                                                    ""
                                                )
                                            val ref = Firebase.database.getReference("scooters")
                                            val scooterCoords = ref.child("$id/coords")
                                            scooterCoords.child("lat").setValue(newLocation.lat)
                                            scooterCoords.child("long").setValue(newLocation.long)
                                        }
                                        val auth = Firebase.auth
                                        val database = Firebase.database.reference

                                        val timestamp = System.currentTimeMillis()

                                        val pastRide = PastRide(
                                            viewModel.currentScooter.value!!, price,
                                            locationTrace, timestamp
                                        )
                                        auth.currentUser?.let { user ->
                                            val uid = database.child("pastrides")
                                                .child(user.uid)
                                                .push()
                                                .key

                                            uid?.let {
                                                database.child("pastrides")
                                                    .child(user.uid)
                                                    .child(it)
                                                    .setValue(pastRide)
                                            }
                                        }
                                        rideState = RideState.SCAN
                                        navController.navigate("PhotoPage/${viewModel.currentScooter.value.toJson()}/")
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text =
                                when (rideState) {
                                    RideState.SCAN -> stringResource(R.string.map_ride_button_scan)
                                    RideState.START -> stringResource(R.string.map_ride_button_start)
                                    RideState.STOP -> stringResource(R.string.map_ride_button_stop)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}