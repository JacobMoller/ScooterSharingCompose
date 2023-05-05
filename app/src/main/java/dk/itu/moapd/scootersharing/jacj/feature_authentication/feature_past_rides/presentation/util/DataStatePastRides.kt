package dk.itu.moapd.scootersharing.jacj.feature_authentication.feature_past_rides.presentation.util

import dk.itu.moapd.scootersharing.jacj.feature_authentication.feature_past_rides.domain.model.PastRide

sealed class DataStatePastRide {
    class Success(val data: MutableList<PastRide>) : DataStatePastRide()
    class Failure(val message: String) : DataStatePastRide()
    object Loading : DataStatePastRide()
    object Empty : DataStatePastRide()
    companion object
}