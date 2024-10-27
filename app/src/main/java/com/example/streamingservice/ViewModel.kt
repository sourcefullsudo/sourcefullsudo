import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase
import java.util.EnumSet.range


class NotesViewModel : ViewModel() {
    private val Context.dataStore by preferencesDataStore(name = "notes")
    private val _createNoteVisible = MutableLiveData(false)
    val createNoteVisible: LiveData<Boolean> = _createNoteVisible
    val _IndicatorVisibility = MutableLiveData(true)
    val IndicatorVisibility: LiveData<Boolean> = _IndicatorVisibility
    fun toggleVisibility() {
        _createNoteVisible.value = _createNoteVisible.value?.not()
    }

    fun toggleIndicatorAppeareance() {
        _IndicatorVisibility.value = true
        Log.i("indicator visibility", _IndicatorVisibility.value.toString())
    }

    fun toggleIndicatorDisappeareance() {
        _IndicatorVisibility.value = false
        Log.i("indicator visibility", _IndicatorVisibility.value.toString())
    }

    var notes = mutableStateListOf<String>()
    var doneNotes = mutableStateListOf<String>()
    var deletedNotes = mutableStateListOf<String>()
    var importantNotes = mutableStateListOf<String>()
    var selectedElementsInDeleted = mutableStateListOf<String>()


    val Refrence1 =
        Firebase.database.getReferenceFromUrl("https://newproject-22c84-default-rtdb.europe-west1.firebasedatabase.app/notes")

    val Reference2 =
        Firebase.database.getReferenceFromUrl("https://newproject-22c84-default-rtdb.europe-west1.firebasedatabase.app/importantNotes")

    val Reference3 =
        Firebase.database.getReferenceFromUrl("https://newproject-22c84-default-rtdb.europe-west1.firebasedatabase.app/deletedNotes")

    val Reference4 =
        Firebase.database.getReferenceFromUrl("https://newproject-22c84-default-rtdb.europe-west1.firebasedatabase.app/doneNotes")

    fun WriteData(
        FunctionExecuted: Int
    ) {
        Log.i("function executed", "writing data")
        //fixed it, now note is not null anymore and it crashes
        //TODO: convert list into accepted format to save to RTDB on firebase


        fun writeToNote() {
            var key = 0
            for (i in notes.indices) {
                key = key + 1
                Log.i("notesVar iterator", i.toString())
                Refrence1.child(key.toString()).setValue(notes[i])
            }
        }

        fun writeToImportantNotes() {
            var key = 0
            for (i in importantNotes.indices) {
                key = key + 1
                Log.i("ImportantNotesVar iterator", i.toString())
                Reference2.child(key.toString()).setValue(importantNotes[i])
            }
        }

        fun writeToDeletedNotes() {
            var key = 0
            for (i in deletedNotes.indices) {
                key = key + 1
                Log.i("deletedNotesVar iterator", i.toString())
                Reference3.child(key.toString()).setValue(deletedNotes[i])
            }
        }

        fun writeToDoneNotes() {
            var key = 0
            for (i in doneNotes.indices) {
                key = key + 1
                Log.i("doneNotesVar iterator", i.toString())
                Reference4.child(key.toString()).setValue(doneNotes[i])
            }
        }


        when (FunctionExecuted) {
            0 -> writeToNote()
            1 -> writeToImportantNotes()
            2 -> writeToDeletedNotes()
            3 -> writeToDoneNotes()
            else -> {
                Log.i("writing failure", "no function to execute at index ${FunctionExecuted}")
            }
        }
    }

    fun ReadData() {
        Log.d("ReadData", "function is being executed")
        // Read from the database
        Refrence1.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.getValue<Any>()
                Log.d("read data", "Value is: " + value)
                Log.d("onDataChange", "function is being executed")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })
    }
}

