import android.support.v4.os.IResultReceiver._Parcel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotesViewModel : ViewModel() {
    private val _createNoteVisible = MutableLiveData(false)
    val createNoteVisible: LiveData<Boolean> = _createNoteVisible

    fun toggleVisibility() {
        _createNoteVisible.value = _createNoteVisible.value?.not()
    }

    var notes = mutableStateListOf<String>()
    var doneNotes = mutableStateListOf<String>()
    var deletedNotes = mutableStateListOf<String>()

}
