package dk.itu.moapd.scootersharing.jacj.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * The scooter object that holds the information about a single scooter.
 *
 * @param name the unique name of the scooter.
 * @param _location the location of the scooter.
 * @param timestamp the timestamp of the creation of the scooter. If no param is provided it will use the current timestamp.
 * @property name ?
 * @property location ?
 * @property timestamp
 * @constructor Creates a scooter, given a name, location (and optional timestamp).
 * @author Jacob Møller Jensen
 * @since 0.3.0
 */
class Coords(var lat: Double? = 0.0, var long: Double? = 0.0)

//TODO: Check that since is right
class Scooter(var name: String? = null, var location: String? = null, var coords: Coords? = null, var timestamp: Long? = null, var image: String = "") {
    /**
     * Formats the scooter saved timestamp to a string
     *
     * @return A string of form "HH:mm dd/MM yyyy". For example "12:00 01/01 1970".
     * @author Jacob Møller Jensen
     * @since 0.3.0
     */
    fun dateFormatted(): String {
        val date = timestamp?.let { Date(it) }
        val format = SimpleDateFormat("HH:mm dd/MM yyyy", Locale.GERMANY)
        return format.format(date).toString()
    }
}