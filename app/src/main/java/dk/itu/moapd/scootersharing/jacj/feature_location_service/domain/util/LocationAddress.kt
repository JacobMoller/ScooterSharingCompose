package dk.itu.moapd.scootersharing.jacj.feature_location_service.domain.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Coords
import java.util.Locale

/**
 * Use Geocoder API to convert the current location into a `String` address, and update the
 * corresponding UI component.
 * @param context A suitable context for the geocoder to consume
 * @param coordinates The current latitude and longitude coordinate.
 */
fun setAddress(context: Context, coordinates: Coords?): String? {
    if (!Geocoder.isPresent())
        return null
    val lat = coordinates?.lat
    val long = coordinates?.long

    var result = ""

    // Create the `Geocoder` instance.
    val geocoder = Geocoder(context, Locale.getDefault())

    // Return an array of Addresses that attempt to describe the area immediately surrounding
    // the given latitude and longitude.
    if (Build.VERSION.SDK_INT >= 33) {
        val geocodeListener = Geocoder.GeocodeListener { addresses ->
            addresses.firstOrNull()?.toStreetNameAndNumberString()?.let { address ->
                result = address
            }
        }
        if (lat != null && long != null) {
            geocoder.getFromLocation(lat,long, 1, geocodeListener)
        }
    }
    else {
        if (lat != null && long != null) {
            geocoder.getFromLocation(lat,long, 1)?.let { addresses ->
                addresses.firstOrNull()?.toStreetNameAndNumberString()?.let { address ->
                    result = address
                }
            }
        }
    }
    return result
}

private fun Address.toStreetNameAndNumberString() : String {
    return "Near " + this.getAddressLine(0)
}