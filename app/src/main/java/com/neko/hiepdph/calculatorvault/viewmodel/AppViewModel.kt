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

    fun createFolder(parentDir: File, fileName: String,onSuccess:()->Unit,onError:(e:String)->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            CreateFile.createFileDirectory(parentDir, fileName,onSuccess,onError)
        }
    }

    fun insertVaultItem(item:FileVaultItem){
        viewModelScope.launch {
            appRepo.insertFileVault(item)
        }
    }

    fun updateVaultItem(item:FileVaultItem){
        viewModelScope.launch {
            appRepo.updateFileVault(item)
        }
    }

    fun deleteFileVault(listId:List<Int>){
        viewModelScope.launch {
            appRepo.deleteFile(listId)
        }
    }


    fun encrypt(
        context: Context,
        listFile: List<File>,
        destination: List<File>,
        targetName: List<String>,
        progress: (value: Float, currentFile: File?) -> Unit,
        onSuccess: (MutableList<String>) -> Unit,
        onError: (t: Throwable) -> Unit,
        encryptMode: Int = EncryptionMode.HIDDEN,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.encrypt(
                context,
                listFile,
                destination,
                targetName,
                0L,
                progress,
                onSuccess,
                onError,
                encryptionMode = encryptMode,
            )
        }
    }

    fun getAllFileFromFolderEncrypted(folderPath: String): LiveData<MutableList<FileVaultItem>> {
        return appRepo.getAllFileInEnCryptFolder(folderPath)
    }

    fun deleteMultipleFolder(
        path: List<String>,onProgress:(value:Float) -> Unit,onSuccess: () -> Unit, onError: (e: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteMultipleFolderInDirectory(path, onProgress,onSuccess, onError)
        }
    }
    fun deleteAllRecyclerBin(path: String, onSuccess: () -> Unit, onError: (e: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteAllChildInDirectory(path, onSuccess, onError)
        }
    }

}