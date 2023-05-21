package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import com.neko.hiepdph.calculatorvault.config.EncryptionMode
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.data.repositories.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PersistentViewModel @Inject constructor(val appRepo: AppRepo) : ViewModel() {

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


    fun getAllFileFromFolderEncrypted(folderPath: String): LiveData<List<FileVaultItem>> {
        return appRepo.getAllFileInEnCryptFolder(folderPath)
    }

    fun updateFileVault(fileVaultItem: FileVaultItem){
        viewModelScope.launch {
            appRepo.updateFileVault(fileVaultItem)
        }
    }

    fun deleteFileVault(listId:MutableList<Int>){
        viewModelScope.launch {
            appRepo.deleteFile(listId)
        }
    }


//    fun getVaultItem():MutableList<FileVaultItem>{
//        return appRepo.getAllFile()
//    }

    fun decryptFile(
        context: Context,
        listFile: MutableList<File>,
        destination: File,
        targetName:MutableList<String>,
        progress: (state: Int, value: Float, currentFile: File?) -> Unit,
        onSuccess: () -> Unit,
        onError: (t: Throwable) -> Unit,
        encryptMode: Int = EncryptionMode.HIDDEN,
        encrypt:Boolean = true
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.copyEncrypt(
                context,
                listFile,
                destination,
                targetName,
                0L,
                progress,
                false,
                onSuccess,
                onError,
                encryptionMode = encryptMode,
                encrypt
            )
        }
    }


}