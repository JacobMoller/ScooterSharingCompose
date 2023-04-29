package dk.itu.moapd.scootersharing.jacj

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.jacj.models.Scooter


const val DATABASE_URL = "https://scooter-sharing-5c9ca-default-rtdb.europe-west1.firebasedatabase.app/"
class MainViewModel : ViewModel() {
    val response: MutableState<DataState> = mutableStateOf(DataState.Empty)

    init {
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        val tempList = mutableListOf<Scooter>()
        response.value = DataState.Loading
        Firebase.database(DATABASE_URL).getReference("scooters")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(DataSnap in snapshot.children) {
                        Log.d("HEEEE", "new scooter")
                        val scooter = DataSnap.getValue(Scooter::class.java)
                        if(scooter != null)
                            tempList.add(scooter)
                    }
                    response.value = DataState.Success(tempList)
                }

                override fun onCancelled(error: DatabaseError) {
                    response.value = DataState.Failure(error.message)
                }
            })
    }


}