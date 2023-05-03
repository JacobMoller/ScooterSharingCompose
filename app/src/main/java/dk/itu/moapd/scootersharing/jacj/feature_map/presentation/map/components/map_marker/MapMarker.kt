package dk.itu.moapd.scootersharing.jacj.feature_map.presentation.map.components.map_marker

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import dk.itu.moapd.scootersharing.jacj.R
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Coords
import dk.itu.moapd.scootersharing.jacj.feature_map.presentation.util.getBitmapDescriptor

@Composable
fun MapMarker(coords: Coords, onClick: (Marker) -> Boolean) {
    Marker(
        state = MarkerState(
            position = LatLng(
                coords.lat!!,
                coords.long!!
            ),
        ),
        icon = getBitmapDescriptor(
            if(isSystemInDarkTheme()) R.drawable.map_scooter_marker_dark else R.drawable.map_scooter_marker_light,
            LocalContext.current
        ),
        tag = 0,
        onClick = onClick
    )
}
