package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.data.repositories.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RecyclerBinViewModel @Inject constructor(val appRepo: AppRepo) : ViewModel() {


    fun deleteFolder(path: String, onSuccess: () -> Unit, onError: (e: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteFolderInDirectory(path, onSuccess, onError)
        }
    }

    fun deleteMultipleFolder(
        path: List<String>,
        onProgress: (value: Float) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<String>, listOfFileDeletedFailed: MutableList<String>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteMultipleFolderInDirectory(path, onResult, onProgress)
        }
    }


    fun getAllFileChildFromFolder(path: String): LiveData<MutableList<FileVaultItem>> {
        return appRepo.getAllFileDeleted(path)

    }

    fun deleteFileVault(listId:List<Int>){
        viewModelScope.launch {
            appRepo.deleteFile(listId)
        }
    }
    fun updateFileVault(fileVaultItem: FileVaultItem){
        viewModelScope.launch {
            appRepo.updateFileVault(fileVaultItem)
        }
    }


    fun restoreFile(
        context: Context,
        files: List<File>?,
        targetFolder: List<File>,
        tSize: Long,
        progress: (value: Float, currentFile: File?) -> Unit,
        onSuccess: (MutableList<String>) -> Unit,
        onError: (t: Throwable) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.copy(
                context, files, targetFolder, tSize, progress, onSuccess, onError
            )
        }
    }

}