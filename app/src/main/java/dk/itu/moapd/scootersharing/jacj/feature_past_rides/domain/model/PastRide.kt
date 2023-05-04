package dk.itu.moapd.scootersharing.jacj.feature_past_rides.domain.model

import dk.itu.moapd.scootersharing.jacj.core.domain.model.Coords
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Scooter

data class PastRide(var scooter: Scooter? = null, var price: Long? = 0, var locationUpdates: MutableList<Coords>? = mutableListOf(), var timestamp: Long? = 0)