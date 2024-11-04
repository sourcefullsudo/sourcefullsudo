@file:Suppress("PackageDirectoryMismatch", "PackageDirectoryMismatch")

package com.example.streamingservice

import NotesViewModel
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.streamingservice.ui.theme.StreamingServiceTheme
import com.example.streamingservice.ui.theme.cardColor
import com.example.streamingservice.ui.theme.addNoteScreenBackgroundColor
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

val nestedScrollConnection = object : NestedScrollConnection {}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        auth.signInAnonymously().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInAnonymously:success")
                val user = auth.currentUser
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInAnonymously:failure", task.exception)
                Toast.makeText(
                    baseContext,
                    "Authentication failed.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
        enableEdgeToEdge()
        setContent {
            StreamingServiceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NotesOverview(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}


//global variables

val maxCharacters = 20
var rowCounter = 1
var sections = listOf(1)

//function called onCreate containing all other gui functions



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesOverview(modifier: Modifier, notesViewModel: NotesViewModel = viewModel()) {

    notesViewModel.readKey()

    // Obligatoric functions, var and logic

    fun isInRange(value: Float, range: IntRange): Boolean {
        return value >= range.first && value <= range.last
    }

    val pagerState = rememberPagerState(pageCount = { 3 })
    val scrollPercentage = calculateScrollPercentage(pagerState)
    Log.i("scrollPercentage var type", scrollPercentage::class.simpleName.toString())

    if (isInRange(scrollPercentage, 25..75)) {
        notesViewModel.toggleIndicatorAppeareance()
        Log.i("condition", "true")
    } else {
        notesViewModel.toggleIndicatorDisappeareance()
        Log.i("condition2", "false")
    }

    //Horizontal Pager

    Log.i("scrolled percentage", scrollPercentage.toString())

    HorizontalPager(
        state = pagerState, modifier = Modifier.fillMaxSize()
    ) { page ->
        // Define content for each page
        when (page) {
            0 -> Trash()
            1 -> Upcoming(modifier = Modifier)
            2 -> PinnedNotes(modifier = Modifier)
        }
    }




    suspend fun ScrollToPage(index: Int) {
        withContext(Dispatchers.Main) {
            if (index >= 0 && index <= pagerState.pageCount) {
                pagerState.scrollToPage(index)
            } else {
                Log.e("Scroll error", "out of index ${index}")
            }
        }

    }


    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    //App Bar

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppBar(modifier: Modifier, notesViewModel: NotesViewModel = viewModel()) {
        var AppBarVisibility by remember {
            mutableStateOf(true)
        }
        val state = rememberSwipeToDismissBoxState()
        val IndicatorVisibility by notesViewModel.IndicatorVisibility.observeAsState(true)
        //val TrashIndicatorVisibility by notesViewModel.TrashIndicatorVisibility.observeAsState(true)

        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomEnd),
                visible = !AppBarVisibility,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                SwipeToDismissBox(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    state = state,
                    backgroundContent = {},
                    enableDismissFromStartToEnd = false
                ) {
                    if (state.currentValue == SwipeToDismissBoxValue.EndToStart) {
                        AppBarVisibility = true
                        Log.d("AppBar Visibility has changed", AppBarVisibility.toString())
                    }
                    FloatingActionButton(
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .padding(12.dp), onClick = {
                            notesViewModel.toggleVisibility()
                            scope.launch {
                                ScrollToPage(1)
                            }
                        }, containerColor = Color.Yellow, contentColor = Color.Black
                    ) {
                        Icon(Icons.Filled.Add, "add note")
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = AppBarVisibility,
                enter = fadeIn() + expandHorizontally() + scaleIn(),
                exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it / 2 }) + scaleOut()
            ) {


                Box(modifier = Modifier
                    .padding(12.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(Color.Black)
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = {
                            AppBarVisibility = false
                            Log.d(
                                "AppBar Visibility long press", AppBarVisibility.toString()
                            )
                            Log.d("Long press", "True")
                        })
                    }) {
                    FloatingActionButton(
                        modifier = Modifier.align(Alignment.Center), onClick = {
                            notesViewModel.toggleVisibility()
                            scope.launch {
                                ScrollToPage(1)
                            }
                        }, containerColor = Color.Yellow, contentColor = Color.Black
                    ) {
                        Icon(Icons.Filled.Add, "add note")
                    }


                    Box(modifier = Modifier.align(Alignment.BottomCenter)) {


                        AnimatedVisibility(
                            visible = IndicatorVisibility,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .width(50.dp)
                                    .align(Alignment.BottomCenter)
                                    .padding(
                                        top = 5.dp, start = 4.dp, bottom = 10.dp, end = 4.dp
                                    )
                                    .clip(shape = RoundedCornerShape(12.dp)),
                                color = Color.Yellow,
                                thickness = 3.dp
                            )
                        }
                    }

                    val backgroundColor by animateColorAsState(
                        targetValue = if (isInRange(
                                scrollPercentage, 0..25
                            )
                        ) Color.DarkGray else Color.Transparent
                    )



                    TextButton(
                        onClick = {
                            scope.launch {
                                ScrollToPage(0)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 50.dp)
                            .background(backgroundColor, shape = RoundedCornerShape(20.dp))
                            .size(70.dp)
                    ) {
                        Column(modifier = Modifier) {


                            Icon(
                                Icons.Filled.Delete,
                                "Deleted",
                                tint = Color.Yellow,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Text(
                                "Trash",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                    val backgroundColorPageNavigation by animateColorAsState(
                        targetValue = if (isInRange(
                                scrollPercentage, 75..100
                            )
                        ) Color.DarkGray else Color.Transparent
                    )
                    TextButton(
                        onClick = {
                            scope.launch {
                                ScrollToPage(2)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 50.dp)
                            .background(
                                backgroundColorPageNavigation, shape = RoundedCornerShape(20.dp)
                            )
                            .size(70.dp)
                    ) {
                        Column(modifier = Modifier) {


                            Icon(
                                Icons.Filled.Info,
                                "Important",
                                tint = Color.Yellow,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Text(
                                "Pinned",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .align(Alignment.CenterHorizontally)
                                    .fillMaxWidth()
                            )
                        }
                    }

                }
            }
        }
    }




    AppBar(modifier = Modifier)
    AddNoteScreen(modifier = Modifier)
}


@Composable
@OptIn(ExperimentalFoundationApi::class)
fun SickyHeader1(text: String) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(), contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .shadow(35.dp)
                .padding(12.dp)
                .border(
                    0.5.dp, color = Color.White, shape = RoundedCornerShape(8.dp)
                )
                .height(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier = Modifier.padding(start = 4.dp, end = 4.dp),
                text = text,
                color = Color.Gray
            )
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AddNoteScreen(modifier: Modifier, viewModel: NotesViewModel = viewModel()) {
    var notes = viewModel.notes
    var newNote by remember { mutableStateOf("") }
    val createNoteVisible by viewModel.createNoteVisible.observeAsState(true)
    var buttonColor by remember { mutableStateOf(Color.Yellow) }


    AnimatedVisibility(
        modifier = Modifier, visible = createNoteVisible, enter = fadeIn(), exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )
    }
    AnimatedVisibility(
        visible = createNoteVisible,
        enter = fadeIn() + expandVertically() + scaleIn(),
        exit = fadeOut() + shrinkVertically() + scaleOut()
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .padding(30.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .background(addNoteScreenBackgroundColor)
                    .fillMaxSize()
                    .align(Alignment.Center)

            ) {
                TextField(modifier = Modifier
                    .padding(
                        top = 60.dp, bottom = 55.dp, end = 12.dp, start = 12.dp
                    )
                    .fillMaxSize(), colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    cursorColor = Color.Yellow,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent

                ), value = newNote, onValueChange = { currentNote -> newNote = currentNote })
                Text(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(20.dp),
                    text = "New Note",
                    fontSize = 30.sp,
                    color = Color.White
                )
                TextButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    onClick = {
                        //add a new note
                        if (newNote != "") {
                            Firebase.analytics.logEvent("note_added", null)
                            viewModel.toggleVisibility()
                            notes += newNote
                            Log.i("note added", viewModel.notes.toString())
                            //write data after newNote has been added to notes list
                            viewModel.writeToNote(newNote)
                            viewModel.ReadData()
                            newNote = ""
                        }
                    }) {
                    Icon(Icons.Filled.Done, "Done", tint = buttonColor)
                }
                TextButton(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    onClick = {
                        newNote = ""
                        viewModel.toggleVisibility()
                    }) {

                    Icon(
                        Icons.Filled.Close, "Done", tint = Color.Yellow,
                    )

                }
                ElevatedButton(
                    onClick = { }, colors = ButtonColors(
                        containerColor = Color.Black,
                        contentColor = Color.Yellow,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Black
                    ), modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                ) {
                    Icon(Icons.Filled.DateRange, "Schedule", tint = Color.Yellow)
                }
            }
        }
    }
    //.background(Color.Black.copy(alpha = 0.35f))

    if (newNote == "") {
        buttonColor = Color.Gray
    } else {
        buttonColor = Color.Yellow
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinnedNotes(modifier: Modifier, notesViewModel: NotesViewModel = viewModel()) {
    var textVisibility by remember {
        mutableStateOf(false)
    }

    var importantNotes = notesViewModel.importantNotes
    var ImportantshortNotes = importantNotes.filter { it.length < maxCharacters }
    var ImportantlongNote = importantNotes.filter { it.length > maxCharacters }
    var ImportantchunkedShortNotes = ImportantshortNotes.chunked(2)
    Box() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        ) {
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = textVisibility,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {

                Text(
                    text = "No Notes yet...", modifier = Modifier.padding(top = 120.dp)
                )
            }
            @OptIn(ExperimentalFoundationApi::class) LazyColumn(
                modifier = Modifier.nestedScroll(
                    nestedScrollConnection
                )
            ) {
                items(1) {
                    if (importantNotes.size > 0) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Important notes",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 40.dp, bottom = 12.dp)
                            )
                        }
                    }
                }

                sections.forEach { section ->
                    //Pinned short
                    items(rowCounter) {
                        ImportantchunkedShortNotes.forEach { chunk ->
                            Row(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                chunk.forEach { note ->
                                    val state = rememberSwipeToDismissBoxState()
                                    var BackgroundText by remember {
                                        mutableStateOf<String>("")
                                    }
                                    //delete Important note
                                    if (state.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                        notesViewModel.importantNotes.remove(note)
                                        notesViewModel.deletedNotes.add(note)
                                        BackgroundText = "Delete"
                                    }
                                    //unpin important note
                                    if (state.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                                        notesViewModel.notes.add(note)
                                        notesViewModel.importantNotes.remove(note)
                                        BackgroundText = "Unpin"
                                    }

                                    SwipeToDismissBox(
                                        modifier = Modifier.weight(1f),
                                        state = state,
                                        backgroundContent = {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(12.dp)
                                            ) {
                                                Text(
                                                    BackgroundText,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                        },
                                    ) {
                                        NoteUi(
                                            modifier = modifier.weight(1f),
                                            CornerRadius = 15.dp,
                                            note = note
                                        )

                                    }

                                }
                            }
                        }
                    }
                    items(ImportantlongNote) {

                        val state = rememberSwipeToDismissBoxState()

                        SwipeToDismissBox(
                            modifier = Modifier,
                            state = state,
                            backgroundContent = {},
                        ) {
                            //delete important note
                            if (state.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                notesViewModel.deletedNotes.add(it)
                                notesViewModel.importantNotes.remove(it)

                            }
                            //unpin important note
                            if (state.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                                notesViewModel.notes.add(it)
                                notesViewModel.importantNotes.remove(it)
                            }

                            val borderColor by animateColorAsState(
                                targetValue = if (state.currentValue == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Yellow
                            )
                            NoteUi(
                                CornerRadius = 25.dp,
                                modifier = Modifier.wrapContentSize(),
                                note = it
                            )
                        }
                    }
                }
            }
        }

    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Upcoming(modifier: Modifier, notesViewModel: NotesViewModel = viewModel()) {

    var isSent by remember {
        mutableStateOf(true)
    }

    var doneNotes = notesViewModel.doneNotes
    var shortDoneNotes = doneNotes.filter { it.length < maxCharacters }
    var longDoneNote = doneNotes.filter { it.length > maxCharacters }
    var chunkedShortDoneNotes = shortDoneNotes.chunked(2)
    var notes = notesViewModel.notes
    val shortNote = notes.filter { it.length < maxCharacters }
    val longNote = notes.filter { it.length > maxCharacters }
    Log.i("vartype", shortNote.toString())
    val ChunkedShortNotes = shortNote.chunked(2)
    var upcomingStickyHeaderVisibility = false
    var doneStickyHeaderVisibility = false
    var textVisibility by remember {
        mutableStateOf(false)
    }
    var _ifStatementCount = listOf(1)

    var OffsetX by rememberSaveable {
        mutableStateOf(0f)
    }
    if (doneNotes.size != 0) {
        doneStickyHeaderVisibility = true
    }
    if (notes.size != 0) {
        upcomingStickyHeaderVisibility = true
    }
    if (notes.size == 0 && doneNotes.size == 0) {

        textVisibility = true

    } else {
        textVisibility = false
    }

    Box() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        ) {
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.TopCenter),
                visible = textVisibility,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {

                Text(
                    text = "No Notes yet...", modifier = Modifier.padding(top = 120.dp)
                )
            }
            @OptIn(ExperimentalFoundationApi::class) LazyColumn(
                modifier = Modifier.nestedScroll(
                    nestedScrollConnection
                )
            ) {
                sections.forEach { section ->


                    //Upcoming
                    stickyHeader {
                        AnimatedVisibility(
                            visible = upcomingStickyHeaderVisibility,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            SickyHeader1(text = "Upcoming")
                        }
                    }
                    items(rowCounter) {
                        ChunkedShortNotes.forEach { chunk ->
                            Row(
                                modifier = Modifier.padding(8.dp)
                            ) {
                                chunk.forEach { note ->
                                    val state = rememberSwipeToDismissBoxState()
                                    var BackgroundText by remember {
                                        mutableStateOf<String>("")
                                    }
                                    //delete short note
                                    if (state.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                        Log.i("delete::note", note)
                                        notes.remove(note)
                                        notesViewModel.deletedNotes.add(note)
                                        Log.i(
                                            "current list state <notes>", notes.toList().toString()
                                        )
                                        BackgroundText = "The note will be moved to trash"
                                        notesViewModel.writeToDeletedNotes(string = note)
                                        Log.i(
                                            "index ${note}",
                                            notesViewModel.notes.indexOf(note).toString()
                                        )
                                        findIndex(Object=note, array = notesViewModel.notes)
                                        notesViewModel.removeFromNote(
                                        index= objectIndex
                                        )
                                    }
                                    //pin short note
                                    if (state.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                                        notesViewModel.importantNotes.add(note)
                                        notes.remove(note)
                                        BackgroundText = "The note will be mov  ed to important"
                                    }

                                    SwipeToDismissBox(
                                        modifier = Modifier.weight(1f),
                                        state = state,
                                        backgroundContent = {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(12.dp)
                                            ) {
                                                Text(
                                                    BackgroundText,
                                                    modifier = Modifier.align(Alignment.Center)
                                                )
                                            }
                                        },
                                    ) {
                                        NoteUi(
                                            modifier = modifier.weight(1f),
                                            CornerRadius = 15.dp,
                                            note = note
                                        )

                                    }

                                }
                            }
                        }
                    }
                    //Long Upcoming notes
                    items(1) {
                        longNote.forEach() {

                            val state = rememberSwipeToDismissBoxState()

                            SwipeToDismissBox(
                                modifier = Modifier,
                                state = state,
                                backgroundContent = {},
                            ) {
                                //delete long note
                                if (state.currentValue == SwipeToDismissBoxValue.EndToStart) {
                                    notesViewModel.deletedNotes.add(it)
                                    notesViewModel.notes.remove(it)
                                    Log.i("executing", "swipe to delete")
                                    notesViewModel.writeToDeletedNotes(string = it)
                                    notesViewModel.removeFromNote(
                                        index = notesViewModel.notes.indexOf(
                                            it
                                        )
                                    )
                                }
                                //pin long note
                                if (state.currentValue == SwipeToDismissBoxValue.StartToEnd) {
                                    notesViewModel.importantNotes.add(it)
                                    notesViewModel.notes.remove(it)

                                }

                                val borderColor by animateColorAsState(
                                    targetValue = if (state.currentValue == SwipeToDismissBoxValue.EndToStart) Color.Red else Color.Yellow
                                )
                                NoteUi(
                                    CornerRadius = 25.dp,
                                    modifier = Modifier.wrapContentSize(),
                                    note = it
                                )
                            }
                        }
                    }

                    //Done Notes

                    //sticky header for Done notes

                    sections.forEach { section ->
                        stickyHeader {
                            AnimatedVisibility(
                                visible = doneStickyHeaderVisibility,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                SickyHeader1(text = "Sent")
                            }
                        }

                        //short Done Notes
                        items(rowCounter) {
                            chunkedShortDoneNotes.forEach { chunk ->
                                Row(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                ) {
                                    chunk.forEach { note ->
                                        NoteUi(
                                            CornerRadius = 15.dp,
                                            modifier = Modifier.weight(1f),
                                            note = note
                                        )
                                    }//editedNote->note=editedNote
                                }
                            }
                        }
                        //Long Done Notes
                        items(1) {
                            longDoneNote.forEach() {
                                NoteUi(
                                    CornerRadius = 25.dp,
                                    modifier = Modifier.wrapContentSize(),
                                    note = it
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoteUi(CornerRadius: Dp, modifier: Modifier, note: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
        ),
        shape = RoundedCornerShape(CornerRadius),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                2.dp, color = Color.Yellow, shape = RoundedCornerShape(CornerRadius)
            )
    ) {
        //SwipeToDismissBox(state = state, backgroundContent = {}) {
        TextField(modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                cursorColor = Color.Yellow,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            value = note,
            onValueChange = { })
    }//editedNote->note=editedNote
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Trash(notesViewModel: NotesViewModel = viewModel()) {
    var deletedNotes = notesViewModel.deletedNotes

    if (deletedNotes.size == 0) {

        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                "Nothing Here",
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(40.dp)
            )
            Box(modifier = Modifier.align(Alignment.Center)) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor, contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(100.dp)
                        .height(50.dp)
                        .border(
                            2.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp)
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.CenterHorizontally),
                        text = "Note"
                    )
                }
                Icon(
                    Icons.Rounded.Delete,
                    "Delete Icon",
                    Modifier
                        .size(240.dp)
                        .align(Alignment.Center)
                        .alpha(0.4f),
                    tint = Color.Red
                )
            }
        }
    }

    Box() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
        ) {
            LazyColumn(modifier = Modifier.padding(12.dp)) {
                items(1) {
                    if (deletedNotes.size > 0) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Deleted Notes",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 40.dp, bottom = 12.dp)
                            )
                        }
                    }
                }
                items(deletedNotes) {
                    NoteUi(
                        CornerRadius = 15.dp, modifier = Modifier.wrapContentSize(), note = it
                    )
                }
            }
        }
    }
}



