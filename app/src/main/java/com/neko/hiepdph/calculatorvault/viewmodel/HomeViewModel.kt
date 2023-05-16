package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.*
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    var listFolder = MutableLiveData(mutableListOf<FileVaultItem>())

    var listGroupImageData = SelfCleaningLiveData<MutableList<FileVaultItem>>()
    var listGroupVideoData = SelfCleaningLiveData<MutableList<FileVaultItem>>()
    var listGroupAudioData = SelfCleaningLiveData<MutableList<FileVaultItem>>()
    var listGroupFileData = SelfCleaningLiveData<MutableList<FileVaultItem>>()

    var listImageItem = SelfCleaningLiveData<MutableList<FileVaultItem>>()
    var listAudioItem = SelfCleaningLiveData<MutableList<FileVaultItem>>()
    var listVideoItem = SelfCleaningLiveData<MutableList<FileVaultItem>>()
    var listFileItem = SelfCleaningLiveData<MutableList<FileVaultItem>>()


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