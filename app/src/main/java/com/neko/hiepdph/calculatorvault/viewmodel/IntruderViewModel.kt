package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class IntruderViewModel @Inject constructor() : ViewModel() {

    private val _listItemList = SelfCleaningLiveData<MutableList<ListItem>>()
    val listItemList: LiveData<MutableList<ListItem>> get() = _listItemList
    fun setListItemData(list: List<ListItem>) {
        Log.d("TAG", "setListItemData: " + list.size)
        _listItemList.postValue(list.toMutableList())
    }

    fun getItemListFromFolder(
       folderPath: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            setListItemData(FileUtils.getFileInDirectory(folderPath))
        }
    }

    fun copyMoveFile(
        context: Context,
        listFile: MutableList<File>,
        destination: File,
        progress: (state: Int, value: Float, currentFile: File?) -> Unit,
        onSuccess: () -> Unit,
        onError: (t: Throwable) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.copy(context, listFile, destination, 0L, progress, true, onSuccess, onError)
        }
    }
}