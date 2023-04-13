package com.neko.hiepdph.calculatorvault.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.data.database.model.BookmarkModel
import com.neko.hiepdph.calculatorvault.data.repositories.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowserViewModel @Inject constructor(
    private val appRepo: AppRepo

) : ViewModel() {

    fun getListBookmark():LiveData<List<BookmarkModel>> = appRepo.getAllBookmark()

    fun deleteBookmark(id: Int) {
        viewModelScope.launch {
            appRepo.deleteBookmark(id)
        }
    }

    fun insertBookmark(bookmarkModel: BookmarkModel){
        viewModelScope.launch {
            appRepo.insertBookmark(bookmarkModel)
        }
    }
}