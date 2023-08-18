package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.common.utils.CreateFile
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.data.repositories.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val appRepo: AppRepo
) : ViewModel() {
    var userActionRate = false
    var shouldShowRate = MutableLiveData(false)
    var progressValue = MutableLiveData(0f)

    fun createFolder(
        parentDir: File, fileName: String, onSuccess: () -> Unit, onError: (e: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CreateFile.createFileDirectory(parentDir, fileName, onSuccess, onError)
        }
    }

    fun insertVaultItem(item: FileVaultItem, action: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepo.insertFileVault(item)
            action?.invoke()
        }
    }

    fun updateVaultItem(item: FileVaultItem) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepo.updateFileVault(item)
        }
    }

    fun deleteFileVault(listId: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            appRepo.deleteFile(listId)
        }
    }


    fun encrypt(
        context: Context,
        listFile: List<File>,
        destination: List<File>,
        targetName: List<String>,
        progress: (currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<String>, listOfFileDeletedFailed: MutableList<String>) -> Unit,
        encryptMode: Int = EncryptionMode.HIDDEN,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("TAG", "encrypt123: " + encryptMode)
            CopyFiles.encrypt(
                context,
                listFile,
                destination,
                targetName,
                0L,
                progress = { value, currentFile ->
                    progressValue.postValue(value)
                    progress(currentFile)
                },
                onResult,
                encryptionMode = encryptMode,
            )
        }
    }

    fun decrypt(
        context: Context,
        listFile: List<File>,
        destination: List<File>,
        targetName: List<String>,
        progress: (currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<String>, listOfFileDeletedFailed: MutableList<String>) -> Unit,
        encryptMode: Int = EncryptionMode.HIDDEN,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.decrypt(
                context,
                listFile,
                destination,
                targetName,
                0L,
                progress = { value, currentFile ->
                    progressValue.postValue(value)
                    progress(currentFile)
                },
                onResult,
                encryptionMode = encryptMode,
            )
        }
    }

    fun unLock(
        context: Context,
        listFile: List<File>,
        destination: List<File>,
        targetName: List<String>,
        progress: (currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<String>, listOfFileDeletedFailed: MutableList<String>) -> Unit,
        encryptMode: Int = EncryptionMode.HIDDEN,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.unLock(
                context,
                listFile,
                destination,
                targetName,
                0L,
                progress = { value, currentFile ->
                    progressValue.postValue(value)
                    progress(currentFile)
                },
                onResult,
                encryptionMode = encryptMode,
            )
        }
    }

    fun restoreFile(
        context: Context,
        files: List<File>?,
        targetFolder: List<File>,
        tSize: Long,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<String>, listOfFileDeletedFailed: MutableList<String>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.copy(
                context, files, targetFolder, tSize, progress, onResult
            )
        }
    }

    fun copy(
        context: Context,
        listFile: List<File>,
        destination: List<File>,
        progress: (currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<String>, listOfFileDeletedFailed: MutableList<String>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.copy(context, listFile, destination, 0L, progress = { value, currentFile ->
                progressValue.postValue(value)
                progress(currentFile)
            }, onResult)
        }

    }

    fun getAllFileFromFolderEncrypted(folderPath: String): LiveData<MutableList<FileVaultItem>> {
        return appRepo.getAllFileInEnCryptFolder(folderPath)
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

    fun deleteAllRecyclerBin(
        path: String,
        onProgress: (value: Float) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<String>, listOfFileDeletedFailed: MutableList<String>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteAllChildInDirectory(path, onResult, onProgress)
        }
    }

    fun updateFileVault(fileVaultItem: FileVaultItem) {
        viewModelScope.launch {
            appRepo.updateFileVault(fileVaultItem)
        }
    }
}