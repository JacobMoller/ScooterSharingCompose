package dk.itu.moapd.scootersharing.jacj.feature_past_rides.presentation.past_rides

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.jacj.DATABASE_URL
import dk.itu.moapd.scootersharing.jacj.feature_past_rides.domain.model.PastRide
import dk.itu.moapd.scootersharing.jacj.feature_past_rides.presentation.util.DataStatePastRide


class PastRidesViewModel : ViewModel() {
    val response: MutableState<DataStatePastRide> = mutableStateOf(DataStatePastRide.Empty)

    init {
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        val pastRideList = mutableListOf<PastRide>()
        response.value = DataStatePastRide.Loading
        val auth = Firebase.auth
        auth.currentUser?.let { user ->
            Firebase.database(DATABASE_URL).getReference("pastrides/${user.uid}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (DataSnap in snapshot.children) {
                            val item = DataSnap.getValue(PastRide::class.java)
                            if (item != null)
                                pastRideList.add(item)
                        }
                        response.value = DataStatePastRide.Success(pastRideList)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        response.value = DataStatePastRide.Failure(error.message)
                    }
                })
        }
    }
}
