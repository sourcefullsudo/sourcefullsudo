package com.example.streamingservice

import NotesViewModel
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

//find index of object to find key to modify and delete data in firebase database

var objectIndex = 0
@Composable
fun findIndex(
    Object: String, array: List<String>, notesViewModel: NotesViewModel = viewModel(
    )
) {
    for (i in notesViewModel.notes){
        Log.i("index", i)
    }
        array.forEachIndexed() { index, currentNote ->
            Log.i("index::type", index.toString())
            Log.i("currentNote::type", currentNote)
            if (currentNote == Object) {
                objectIndex = index
            }
        }
}

//calculate the percentage of scrolling for the horizontal pager

@OptIn(ExperimentalFoundationApi::class)
fun calculateScrollPercentage(pagerState: PagerState): Float {
    val currentPageOffset = pagerState.currentPageOffsetFraction
    val currentPage = pagerState.currentPage
    val totalPages = pagerState.pageCount

    return ((currentPage + currentPageOffset) / (totalPages - 1)) * 100
}