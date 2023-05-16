package com.neko.hiepdph.calculatorvault.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.data.repositories.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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


//    fun getVaultItem():MutableList<FileVaultItem>{
//        return appRepo.getAllFile()
//    }


}