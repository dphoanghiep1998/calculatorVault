package com.neko.hiepdph.calculatorvault.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.CreateFile
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
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

    fun getAllFileFromFolderEncrypted(folderPath: String): LiveData<MutableList<FileVaultItem>> {
        return appRepo.getAllFileInEnCryptFolder(folderPath)
    }

}