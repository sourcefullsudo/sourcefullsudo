package com.example.streamingservice

import NotesViewModel
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.streamingservice.ui.theme.Purple40
import com.example.streamingservice.ui.theme.StreamingServiceTheme
import com.example.streamingservice.ui.theme.cardColor
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
fun NotesOverview(modifier: Modifier, viewModel: NotesViewModel = viewModel()) {

    val pagerState = rememberPagerState(pageCount = {4})
    Box(modifier = Modifier.fillMaxSize()){

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        // Define content for each page
        Upcoming(modifier = Modifier.fillMaxSize())
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
                    0.5.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
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


@Composable
fun AppBar(modifier: Modifier, notesViewModel: NotesViewModel = viewModel()) {
    var IndicatorVisibility by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .padding(12.dp)
                .clip(RoundedCornerShape(25.dp))
                .background(Color.Black)
                .fillMaxWidth()
                .height(100.dp)
                .align(Alignment.BottomCenter)
        ) {
            FloatingActionButton(modifier = Modifier.align(Alignment.Center), onClick = {
                notesViewModel.toggleVisibility()
                IndicatorVisibility = !IndicatorVisibility
            }, containerColor = Color.Yellow, contentColor = Color.Black) {
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
                            .padding(top = 5.dp, start = 4.dp, bottom = 10.dp, end = 4.dp)
                            .clip(shape = RoundedCornerShape(12.dp)),
                        color = Color.Yellow,
                        thickness = 3.dp
                    )
                }
            }
            TextButton(
                onClick = {},
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 50.dp),
            ) {
                Column {


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
        }
    }
}

@Composable
fun AddNoteScreen(modifier: Modifier, viewModel: NotesViewModel = viewModel()) {
    var notes = viewModel.notes
    var newNote by remember { mutableStateOf("") }
    val createNoteVisible by viewModel.createNoteVisible.observeAsState(true)
    var buttonColor by remember { mutableStateOf(Color.Yellow) }
    AnimatedVisibility(
        visible = createNoteVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .padding(30.dp)
                    .clip(RoundedCornerShape(25.dp))
                    .border(0.5.dp, color = Color.Yellow, shape = RoundedCornerShape(25.dp))
                    .background(Color.Black)
                    .fillMaxSize()
                    .align(Alignment.Center)
            ) {
                TextField(modifier = Modifier
                    .padding(top = 60.dp, bottom = 55.dp, end = 12.dp, start = 12.dp)
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
                        .padding(20.dp), text = "New Note", fontSize = 30.sp, color = Color.White
                )
                TextButton(modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp), onClick = {
                    if (newNote != "") {
                        viewModel.toggleVisibility()
                        notes += newNote
                        newNote = ""
                    }
                    Log.i("note added", viewModel.notes.toString())
                }) {
                    Icon(Icons.Filled.Done, "Done", tint = buttonColor)
                }
                TextButton(modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp), onClick = {
                    newNote = ""
                    viewModel.toggleVisibility()
                }) {

                    Icon(
                        Icons.Filled.Close, "Done", tint = Color.Yellow,
                    )

                }
                ElevatedButton(
                    onClick = {  },
                    colors = ButtonColors(
                        containerColor = Color.Black,
                        contentColor = Color.Yellow,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.Black
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp)
                ) {
                    Icon(Icons.Filled.DateRange, "Schedule", tint = Color.Yellow)
                }
            }
        }
    }
    if (newNote == "") {
        buttonColor = Color.Gray
    } else {
        buttonColor = Color.Yellow
    }

}


@Composable
fun Upcoming(modifier: Modifier, notesViewModel: NotesViewModel = viewModel()) {
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

    Box(modifier = Modifier.fillMaxSize()) {

        Box(modifier = Modifier.align(Alignment.TopCenter)) {

            AnimatedVisibility(
                visible = textVisibility,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {

                Text(
                    text = "No Notes yet...", modifier = Modifier
                        .padding(top = 120.dp)
                )
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {

        @OptIn(ExperimentalFoundationApi::class)
        LazyColumn {

            sections.forEach { section ->
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
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                        ) {
                            chunk.forEach { note ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = cardColor,
                                    ),
                                    shape = RoundedCornerShape(15.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .border(
                                            2.dp,
                                            color = Color.Yellow,
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                ) {
                                    Row() {

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

                                            value = note, onValueChange = { })




                                        TextButton(onClick = {
                                            notesViewModel.deletedNotes += note
                                            Log.i(
                                                "deleted note",
                                                notesViewModel.deletedNotes.toList().toString()
                                            )
                                            notesViewModel.notes.remove(note)
                                            Log.i("notes", notesViewModel.notes.toList().toString())
                                            Log.i("iterator", note)
                                        }, modifier = Modifier) {
                                            Icon(Icons.Filled.Delete, "Delete Note")
                                        }
                                    }
                                }//editedNote->note=editedNote
                            }
                        }
                    }
                }
                items(1) {
                    longNote.forEach() {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = cardColor,
                                contentColor = Color.White
                            ), shape = RoundedCornerShape(25.dp), modifier = Modifier
                                .padding(8.dp)
                                .wrapContentSize()
                                .fillMaxWidth()
                                .border(
                                    2.dp,
                                    color = Color.Yellow,
                                    shape = RoundedCornerShape(25.dp)
                                )

                        ) {
                            TextField(
                                modifier = Modifier
                                    .padding(12.dp)
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

                                value = it,
                                onValueChange = {})
                        }
                    }
                }

                //Done notes

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
                    items(rowCounter) {
                        chunkedShortDoneNotes.forEach { chunk ->
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                            ) {
                                chunk.forEach { note ->
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = cardColor,
                                        ),
                                        shape = RoundedCornerShape(15.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(8.dp)
                                            .fillMaxWidth()
                                            .border(
                                                2.dp,
                                                color = Color.Yellow,
                                                shape = RoundedCornerShape(15.dp)
                                            )
                                    ) {
                                        Text(text = note, modifier = Modifier.padding(12.dp))
                                    }
                                }//editedNote->note=editedNote
                            }
                        }
                    }
                    items(1) {
                        longDoneNote.forEach() {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = cardColor,
                                    contentColor = Color.White
                                ), shape = RoundedCornerShape(25.dp), modifier = Modifier
                                    .padding(8.dp)
                                    .wrapContentSize()
                                    .fillMaxWidth()
                                    .border(
                                        2.dp,
                                        color = Color.Yellow,
                                        shape = RoundedCornerShape(25.dp)
                                    )

                            ) {
                                TextField(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .padding(12.dp)
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

                                    value = it,
                                    onValueChange = {})
                            }
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Trash(notesViewModel: NotesViewModel = viewModel()) {
    var deletedNotes = notesViewModel.deletedNotes
    var shortDeletedNotes = deletedNotes.filter { it.length < maxCharacters }
    var longDeletedNotes = deletedNotes.filter { it.length > maxCharacters }
    var rowCounterDeletedNotes = shortDeletedNotes.chunked(2)

    LazyColumn(modifier = Modifier.padding(12.dp)) {
        items(rowCounterDeletedNotes) { chunk ->
            Row {
                chunk.forEach {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = cardColor,
                            contentColor = Color.White
                        ), shape = RoundedCornerShape(25.dp), modifier = Modifier
                            .padding(8.dp)
                            .wrapContentSize()
                            .fillMaxWidth()
                            .border(
                                2.dp,
                                color = Color.Yellow,
                                shape = RoundedCornerShape(25.dp)
                            )

                    ) {
                        Text(text = it)
                    }
                }
            }
        }

        items(longDeletedNotes) {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = cardColor,
                    contentColor = Color.White
                ), shape = RoundedCornerShape(25.dp), modifier = Modifier
                    .padding(8.dp)
                    .wrapContentSize()
                    .fillMaxWidth()
                    .border(
                        2.dp,
                        color = Color.Yellow,
                        shape = RoundedCornerShape(25.dp)
                    )

            ) {
                Text(text = "Note")
            }
        }
    }

}