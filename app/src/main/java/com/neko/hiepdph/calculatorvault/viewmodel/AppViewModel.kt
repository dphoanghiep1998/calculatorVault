package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
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
        listItemSelected: List<FileVaultItem>,
        destination: List<File>,
        targetName: List<String>,
        progress: (currentFile: File?) -> Unit,
        onResult: (listOfFileSuccess: MutableList<FileVaultItem>, listOfFileFailed: MutableList<FileVaultItem>) -> Unit,
        encryptMode: Int = EncryptionMode.HIDDEN,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.encrypt(
                context,
                listItemSelected,
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
        listFileVault: List<FileVaultItem>,
        destination: List<File>,
        targetName: List<String>,
        progress: (currentFile: File?) -> Unit,
        onResult: (listOfFileVaultSuccess: MutableList<FileVaultItem>, listOfFileVaultFailed: MutableList<FileVaultItem>) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.decrypt(
                context,
                listFileVault,
                destination,
                targetName,
                0L,
                progress = { value, currentFile ->
                    progressValue.postValue(value)
                    progress(currentFile)
                },
                onResult,
            )
        }
    }

    fun unLock(
        context: Context,
        listVaultFile: List<FileVaultItem>,
        destination: List<File>,
        targetName: List<String>,
        progress: (currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<FileVaultItem>, listOfFileDeletedFailed: MutableList<FileVaultItem>) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.unLock(
                context,
                listVaultFile,
                destination,
                targetName,
                0L,
                progress = { value, currentFile ->
                    progressValue.postValue(value)
                    progress(currentFile)
                },
                onResult,
            )
        }
    }

    fun restoreFile(
        context: Context,
        files: List<FileVaultItem>,
        targetFolder: List<File>,
        progress: (value: Float, currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<FileVaultItem>, listOfFileDeletedFailed: MutableList<FileVaultItem>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.copy(
                context, files, targetFolder, progress = { value, currentFile ->
                    progressValue.postValue(value)
                    progress(value, currentFile)
                }, onResult, isMove = true
            )
        }
    }

    fun copy(
        context: Context,
        listFile: List<FileVaultItem>,
        destination: List<File>,
        progress: (currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<FileVaultItem>, listOfFileDeletedFailed: MutableList<FileVaultItem>) -> Unit,
        isMove: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.copy(context, listFile, destination, progress = { value, currentFile ->
                progressValue.postValue(value)
                progress(currentFile)
            }, onResult, isMove)
        }
    }

    fun delete(
        context: Context,
        listFile: List<FileVaultItem>,
        destination: List<File>,
        progress: (currentFile: File?) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<FileVaultItem>, listOfFileDeletedFailed: MutableList<FileVaultItem>) -> Unit,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.delete(context, listFile, destination, progress = { value, currentFile ->
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
            FileUtils.deleteMultipleFolderInDirectory(path, onResult, onProgress = { value ->
                progressValue.postValue(value * 100)
                onProgress(value * 100)
            })
        }
    }

    fun deleteAllRecyclerBin(
        path: String,
        onProgress: (value: Float) -> Unit,
        onResult: (listOfFileDeletedSuccess: MutableList<String>, listOfFileDeletedFailed: MutableList<String>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteAllChildInDirectory(path, onResult, onProgress = { value ->
                progressValue.postValue(value * 100)
                onProgress(value * 100)
            })
        }
    }

    fun updateFileVault(fileVaultItem: FileVaultItem) {
        viewModelScope.launch {
            appRepo.updateFileVault(fileVaultItem)
        }
    }
}