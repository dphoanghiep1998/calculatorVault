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
class PersistentViewModel @Inject constructor() : ViewModel() {
    private val _listItemListPersistent = SelfCleaningLiveData<MutableList<ListItem>>()
    val listItemListPersistent: LiveData<MutableList<ListItem>> get() = _listItemListPersistent
    fun setListItemPersistentData(list: MutableList<ListItem>) {
        _listItemListPersistent.postValue(list)
    }


    fun getImageChildFromFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listImageChild =
                FileUtils.getFileInDirectory(path).toMutableList()
            setListItemPersistentData(listImageChild)
        }
    }

    fun getAudioChildFromFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listAudioChild =
                FileUtils.getFileInDirectory(path).toMutableList()
            setListItemPersistentData(listAudioChild)
        }
    }

    fun getVideoChildFromFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listVideoChild =
                FileUtils.getFileInDirectory(path).toMutableList()
            setListItemPersistentData(listVideoChild)

        }
    }

    fun getFileChildFromFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val listFileChild =
                FileUtils.getFileInDirectory(path).toMutableList()
            setListItemPersistentData(listFileChild)
        }
    }



}