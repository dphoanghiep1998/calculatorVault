package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.utils.*
import com.neko.hiepdph.calculatorvault.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RecyclerBinViewModel @Inject constructor() : ViewModel() {
    private val _listItemListRecyclerBin = SelfCleaningLiveData<MutableList<ListItem>>()
    val listItemListRecyclerBin: LiveData<MutableList<ListItem>> get() = _listItemListRecyclerBin
    fun setListItemRecyclerBinData(list: MutableList<ListItem>) {
        _listItemListRecyclerBin.postValue(list)
    }

    fun deleteFolder(path: String, callback: IDeleteFile) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteFolderInDirectory(path, callback)
        }
    }
    fun deleteMultipleFolder(path: List<String>, callback: IDeleteFile) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteMultipleFolderInDirectory(path, callback)
        }
    }


    fun getAllFileChildFromFolder(path: String){
        viewModelScope.launch(Dispatchers.IO) {
            val listFileChild =
                FileUtils.getFileInDirectory(path).toMutableList()
            setListItemRecyclerBinData(listFileChild)
        }
    }



}