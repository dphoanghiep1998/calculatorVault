package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.R
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.utils.*
import com.neko.hiepdph.calculatorvault.data.model.VaultDir
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class VaultViewModel @Inject constructor() : ViewModel() {
    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _listFolderInVault = MutableLiveData<MutableList<VaultDir>>()
    val listFolderInVault: LiveData<MutableList<VaultDir>> = _listFolderInVault
    fun setDataToListFolderVault(data: MutableList<VaultDir>) {
        _listFolderInVault.postValue(data)
    }

    fun selfListFolderInVaultPostValue() {
        _listFolderInVault.postValue(_listFolderInVault.value)
    }


    fun getListFolderInVault(context: Context, parentDir: File) {
        viewModelScope.launch(Dispatchers.IO) {
            val listVaultFolder = mutableListOf<VaultDir>()
            val listFile = FileUtils.getFoldersInDirectory(parentDir.path)
            var type: String
            for (file in listFile) {
                Log.d("TAG", "getListFolderInVault: " + file.name)
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
                    Constant.RECYCLER_BIN_FOLDER_NAME -> continue
                    Constant.INTRUDER_FOLDER_NAME -> continue
                    Constant.DECRYPT_FOLDER_NAME -> continue

                    else -> {
                        type = Constant.TYPE_ADD_MORE
                        file.name
                    }
                }
                val count = file.listFiles()?.size ?: 0

                Log.d("TAG", "getListFolderInVault: " +file.absolutePath)
                listVaultFolder.add(
                    VaultDir(
                        file.absolutePath,
                        name,
                        type,
                        true,
                        count,
                        file.length(),
                        file.lastModified()
                    )
                )
            }
            setDataToListFolderVault(listVaultFolder)
        }
    }

    fun deleteFolder(path: String,onSuccess: () -> Unit,onError: (e: String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.deleteFolderInDirectory(path, onSuccess,onError)
        }
    }

    fun createFolder(file: File, name: String, onSuccess: () -> Unit = {},onError: (e: String) -> Unit ={}) {
        viewModelScope.launch(Dispatchers.IO) {
            CreateFile.createFileDirectory(file, name, onSuccess,onError)
        }
    }

    fun renameFolder(file: File,name:String,onSuccess:()->Unit,onError:(e:String)->Unit){
        viewModelScope.launch (Dispatchers.IO){
            FileUtils.renameFile(file,name,onSuccess, onError)
        }
    }
}