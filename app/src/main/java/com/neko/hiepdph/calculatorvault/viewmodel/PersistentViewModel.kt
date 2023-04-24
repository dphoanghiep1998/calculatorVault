package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import com.neko.hiepdph.calculatorvault.data.model.ListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersistentViewModel @Inject constructor() : ViewModel() {
    private val _listItemListPersistent = SelfCleaningLiveData<MutableList<ListItem>>()
    val listItemListPersistent: LiveData<MutableList<ListItem>> get() = _listItemListPersistent
    fun setListItemPersistentData(list: MutableList<ListItem>) {
        _listItemListPersistent.postValue(list)
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


    fun getImageChildFromFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listImageChild = FileUtils.getFileInDirectory(path).toMutableList()
            setListItemPersistentData(listImageChild)
        }
    }

    fun getAudioChildFromFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listAudioChild = FileUtils.getFileInDirectory(path).toMutableList()
            setListItemPersistentData(listAudioChild)
        }
    }

    fun getVideoChildFromFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listVideoChild = FileUtils.getFileInDirectory(path).toMutableList()
            setListItemPersistentData(listVideoChild)

        }
    }

    fun getFileChildFromFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listFileChild = FileUtils.getFileInDirectory(path).toMutableList()
            setListItemPersistentData(listFileChild)
        }
    }

    fun getAllFileChildFromFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listFileChild = FileUtils.getFileInDirectory(path).toMutableList()
            setListItemPersistentData(listFileChild)
        }
    }


}