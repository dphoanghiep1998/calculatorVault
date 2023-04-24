package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import com.neko.hiepdph.calculatorvault.data.model.ListItem
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

    fun deleteFolder(path: String, onSuccess: () -> Unit, onError: (e: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteFolderInDirectory(path, onSuccess, onError)
        }
    }

    fun deleteMultipleFolder(
        path: List<String>, onSuccess: () -> Unit, onError: (e: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteMultipleFolderInDirectory(path, onSuccess, onError)
        }
    }


    fun getAllFileChildFromFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listFileChild = FileUtils.getFileInDirectory(path).toMutableList()
            setListItemRecyclerBinData(listFileChild)
        }
    }

    fun deleteAllRecyclerBin(path: String, onSuccess: () -> Unit, onError: (e: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteAllChildInDirectory(path, onSuccess, onError)
        }
    }

    fun deleteSelectedFile(
        listPath: List<String>, onSuccess: () -> Unit, onError: (e: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteMultipleFolderInDirectory(listPath, onSuccess, onError)
        }
    }

    fun restoreFile(
        context: Context,
        files: MutableList<File>?,
        targetFolder: MutableList<File>,
        tSize: Long,
        progress: (state: Int, value: Float, currentFile: File?) -> Unit,
        isMove: Boolean = false,
        onSuccess: () -> Unit,
        onError: (t: Throwable) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.copy(context,files,targetFolder,tSize,progress,isMove,onSuccess,onError)
        }
    }

}