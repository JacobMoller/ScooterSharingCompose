package dk.itu.moapd.scootersharing.jacj.core.domain.model

/**
 * The scooter object that holds the information about a single scooter.
 *
 * @param name the unique name of the scooter.
 * @param coords the coordinates of the scooter.
 * @constructor Creates a scooter, given a name and coordinates.
 * @author Jacob Møller Jensen
 * @since 0.3.0
 */
data class Scooter(var coords: Coords? = null, var name: String? = null)