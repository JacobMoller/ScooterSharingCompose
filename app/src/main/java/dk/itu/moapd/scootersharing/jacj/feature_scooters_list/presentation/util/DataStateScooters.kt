package dk.itu.moapd.scootersharing.jacj.feature_scooters_list.presentation.util

import dk.itu.moapd.scootersharing.jacj.core.domain.model.Scooter

sealed class DataStateScooter {
    class Success(val data: MutableList<Scooter>) : DataStateScooter()
    class Failure(val message: String) : DataStateScooter()
    object Loading : DataStateScooter()
    object Empty : DataStateScooter()
    companion object
}

