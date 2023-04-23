package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.*
import com.neko.hiepdph.calculatorvault.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    var listFolder = MutableLiveData(mutableListOf<FileDirItem>())

    var listGroupImageData = SelfCleaningLiveData<MutableList<FileDirItem>>()
    var listGroupVideoData = SelfCleaningLiveData<MutableList<FileDirItem>>()
    var listGroupAudioData = SelfCleaningLiveData<MutableList<FileDirItem>>()
    var listGroupFileData = SelfCleaningLiveData<MutableList<FileDirItem>>()

    var listImageItem = SelfCleaningLiveData<MutableList<FileDirItem>>()
    var listAudioItem = SelfCleaningLiveData<MutableList<FileDirItem>>()
    var listVideoItem = SelfCleaningLiveData<MutableList<FileDirItem>>()
    var listFileItem = SelfCleaningLiveData<MutableList<FileDirItem>>()


    fun createFolder(
        parentDir: File, fileName: String, onSuccess: () -> Unit, onError: (e: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CreateFile.createFileDirectory(parentDir, fileName, onSuccess, onError)
        }
    }

    fun deleteFolder(path: String, onSuccess: () -> Unit,onError: (e: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteFolderInDirectory(path, onSuccess,onError)
        }
    }

}