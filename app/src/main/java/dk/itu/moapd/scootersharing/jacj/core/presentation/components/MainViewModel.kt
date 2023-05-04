package dk.itu.moapd.scootersharing.jacj.core.presentation.components

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.jacj.DATABASE_URL
import dk.itu.moapd.scootersharing.jacj.feature_scooters_list.presentation.util.DataStateScooter
import dk.itu.moapd.scootersharing.jacj.core.domain.model.Scooter
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val response: MutableState<DataStateScooter> = mutableStateOf(DataStateScooter.Empty)
    val currentScooter: MutableState<Scooter?> = mutableStateOf(null)
    val storage = Firebase.storage

    init {
        viewModelScope.launch {
            fetchDataFromFirebase()
        }
    }

    private fun fetchDataFromFirebase() {
        val scooterList = mutableListOf<Scooter>()
        response.value = DataStateScooter.Loading
        var auth = Firebase.auth
        auth.currentUser?.let {
            Firebase.database(DATABASE_URL).getReference("scooters")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                            for (DataSnap in snapshot.children) {
                                val item = DataSnap.getValue(Scooter::class.java)
                                if (item != null)
                                    scooterList.add(item)
                            }
                            response.value = DataStateScooter.Success(scooterList)

                    }

                    override fun onCancelled(error: DatabaseError) {
                        response.value = DataStateScooter.Failure(error.message)
                    }
                })
        }
    }


}

