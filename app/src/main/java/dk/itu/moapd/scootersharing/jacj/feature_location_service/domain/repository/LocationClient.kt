package dk.itu.moapd.scootersharing.jacj.feature_location_service.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long) : Flow<Location>

    class LocationException(message: String): Exception()
}