package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.common.utils.ICreateFile
import com.neko.hiepdph.calculatorvault.common.utils.IDeleteFile
import com.neko.hiepdph.calculatorvault.data.model.VaultFileDirItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor() : ViewModel() {
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _listFolderInVault = MutableLiveData<MutableList<VaultFileDirItem>>()
    val listFolderInVault: LiveData<MutableList<VaultFileDirItem>> = _listFolderInVault
    fun setDataToListFolderVault(data: MutableList<VaultFileDirItem>) {
        _listFolderInVault.postValue(data)
    }
    fun selfListFolderInVaultPostValue(){
        _listFolderInVault.postValue(_listFolderInVault.value)
    }

    fun getListFolderInVault(context: Context, parentDir: File) {
        viewModelScope.launch(Dispatchers.IO) {
            val listVaultFolder = mutableListOf<VaultFileDirItem>()

            val listFile = FileUtils.getFoldersInDirectory(parentDir.path)

            var type: String
            for (file in listFile) {
                val name = when (file.name) {
                    Constant.PICTURE_FOLDER_NAME -> {
                        type = Constant.TYPE_PICTURE
                        context.getString(R.string.pictures)
                    }
                    Constant.AUDIOS_FOLDER_NAME -> {
                        type = Constant.TYPE_AUDIOS
                        context.getString(R.string.audios)
                    }
                    Constant.VIDEOS_FOLDER_NAME -> {
                        type = Constant.TYPE_VIDEOS
                        context.getString(R.string.videos)
                    }
                    Constant.FILES_FOLDER_NAME -> {
                        type = Constant.TYPE_FILE
                        context.getString(R.string.files)
                    }
                    else -> {
                        type = Constant.TYPE_ADD_MORE
                        file.name
                    }
                }
                val count = file.listFiles()?.size ?: 0

                listVaultFolder.add(
                    VaultFileDirItem(
                        file.absolutePath, name, type, true, count, file.length(), file.lastModified()
                    )
                )
            }
            setDataToListFolderVault(listVaultFolder)
        }
    }

    fun deleteFolder(path: String, callback: IDeleteFile) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteFolderInDirectory(path, callback)
        }
    }

    fun createFolder(file: File, name: String, callback: ICreateFile) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.createSecretFile(file, name, callback)
        }
    }
}