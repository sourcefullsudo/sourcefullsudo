import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import com.google.firebase.FirebaseApp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamingservice.BackendManagement
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.C
import java.text.DateFormat.getDateInstance
import java.util.Date


class NotesViewModel : ViewModel() {


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


//calculate the percentage of scrolling for the horizontal pager

    @OptIn(ExperimentalFoundationApi::class)
    fun calculateScrollPercentage(pagerState: PagerState): Float {
        val currentPageOffset = pagerState.currentPageOffsetFraction
        val currentPage = pagerState.currentPage
        val totalPages = pagerState.pageCount

        return ((currentPage + currentPageOffset) / (totalPages - 1)) * 100
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

}

