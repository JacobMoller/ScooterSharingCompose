package dk.itu.moapd.scootersharing.jacj

import dk.itu.moapd.scootersharing.jacj.models.Scooter

sealed class DataState {
    class Success(val data: MutableList<Scooter>) : DataState()
    class Failure(val message: String) : DataState()
    object Loading : DataState()
    object Empty : DataState()
}