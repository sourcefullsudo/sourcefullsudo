import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.example.streamingservice.ApplicationStarted
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.database.values
import com.google.firebase.ktx.Firebase
import java.security.Key
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.Date
import java.util.EnumSet.range


class NotesViewModel : ViewModel() {
    private val Context.dataStore by preferencesDataStore(name = "notes")
    private val _createNoteVisible = MutableLiveData(false)
    val createNoteVisible: LiveData<Boolean> = _createNoteVisible
    val _IndicatorVisibility = MutableLiveData(true)
    val IndicatorVisibility: LiveData<Boolean> = _IndicatorVisibility

    val date = Date() // current date and time
    val formatter = getDateInstance()
    val localTimeStr = formatter.format(date)

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

    val KeyRefrence1 =
        Firebase.database.getReferenceFromUrl("https://newproject-22c84-default-rtdb.europe-west1.firebasedatabase.app/keys/notesKey")

    var noteskey = 0

    val Refrence2 =
        Firebase.database.getReferenceFromUrl("https://newproject-22c84-default-rtdb.europe-west1.firebasedatabase.app/pinnedNotes")

    val KeyRefrence2 =
        Firebase.database.getReferenceFromUrl("https://newproject-22c84-default-rtdb.europe-west1.firebasedatabase.app/keys/importantNotesKey")

    var pinnedNotesKey = 0

    val Refrence3 =
        Firebase.database.getReferenceFromUrl("https://newproject-22c84-default-rtdb.europe-west1.firebasedatabase.app/deletedNotes")

    val KeyRefrence3 =
        Firebase.database.getReferenceFromUrl("https://newproject-22c84-default-rtdb.europe-west1.firebasedatabase.app/deletedNotes")

    val deletedNotesKey = 0

    fun storeKey() {
        Log.i("key.current", noteskey.toString())
        KeyRefrence1.child("note").setValue(noteskey)
        noteskey += 1
    }

    fun readKey() {
        Log.i("reading key", localTimeStr)

        KeyRefrence1.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                var value = 0
                for (i in snapshot.getValue<Any>().toString().toList()) {
                    Log.i("key data", i.toString())
                    if (i.isDigit()) {
                        value = i.digitToInt()
                        break
                    }
                }
                Log.d("read key data", value.toString())
                if (ApplicationStarted) {
                    value += 1
                    noteskey = value
                    ApplicationStarted = false
                    Log.i("getting key (on start event)", value.toString())
                } else {
                    Log.i("backup Read", value.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to get key.", error.toException())
            }

        })
    }

    fun writeToNote(string: String) {
        Log.i("writing", "writing notes ${string}")
        Refrence1.child(noteskey.toString()).setValue(string)
        storeKey()
    }

    fun removeFromNote(index: Int) {
        Log.i("removing value", index.toString())
        Refrence1.child(index.toString()).removeValue()
    }

    //IMPORTANT NOTES

    fun writeToImportantNotes(string: String) {
        Log.i("writing", "writing notes ${string}")
        Refrence1.child(pinnedNotesKey.toString()).setValue(string)
        storeKey()
    }

    fun removeFromImportantNote(index: Int) {
        Refrence2.child(index.toString()).removeValue()
    }

    // DELETED NOTES
    fun writeToDeletedNotes(string: String) {
        Log.i("writing to deleted notes", string)
    }

    fun removeFromDeletedNote(index: Int) {
        Refrence3.child(index.toString()).removeValue()
    }

    //DONE NOTES

    fun writeToDoneNotes() {
        Log.i("writing", "writing done notes ${doneNotes.toList()}")
        Refrence1.child("doneNotes").setValue(doneNotes)

    }


    fun ReadData(Refrence: DatabaseReference) {
        Log.d("ReadData", "function is being executed")
        // Read from the database
        Refrence.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (childSnapshot in snapshot.children) {
                    val value = childSnapshot.getValue<Any>()
                    if (value != null) {
                        notes.add(value.toString()) // Add the retrieved value to the list
                        Log.i("retrieved value", value.toString())
                    }
                }

                Log.i("data Snapshot", snapshot.children.toString())
                var value = snapshot.getValue<Any>()
                Log.d("read data for notes", value.toString())
                Log.d("onDataChange", "function is being executed")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })
    }
}

