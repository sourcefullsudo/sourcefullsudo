package com.example.streamingservice

import NotesViewModel
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class BackendManagement {

    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())


    fun Routine(notesViewModel: NotesViewModel) {
        val notes = notesViewModel.notes
        Log.i("Routine", "Routine Started")
        WriteToDatabase(notes)
    }

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun WriteToDatabase(
        data: MutableList<String>
    ) {
        var map = mutableMapOf<String, Any>()
        for (i in data.indices) {
            map[i.toString()] = data[i]
        }
        val dbReference = db.collection("users").document("user1")
        dbReference.set(map).addOnSuccessListener { documentReference ->
            Log.i("Database", "writing to database")
            Log.d("Firestore", "DocumentSnapshot added with ID: $documentReference")
        }.addOnFailureListener { e ->
            Log.i("Database", "writing to database")
            Log.w("Firestore", "Error adding document", e)
        }
        return
    }
}
