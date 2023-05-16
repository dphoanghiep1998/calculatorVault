package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.common.utils.MediaStoreUtils
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
class ListItemViewModel @Inject constructor(val repo: AppRepo) : ViewModel() {

    private val _listItemList = SelfCleaningLiveData<MutableList<FileVaultItem>>()
    val listItemList: LiveData<MutableList<FileVaultItem>> get() = _listItemList
    fun setListItemData(list: List<FileVaultItem>) {
        Log.d("TAG", "setListItemData: " + list.size)
        _listItemList.postValue(list.toMutableList())
    }

    fun getItemListFromFolder(
        context: Context, folderPath: String, type: String, fileType: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            when (type) {
                Constant.TYPE_PICTURE -> {
                    setListItemData(MediaStoreUtils.getChildImageFromPath(context, folderPath))
                }
                Constant.TYPE_AUDIOS -> {
                    setListItemData(MediaStoreUtils.getChildAudioFromPath(context, folderPath))
                }
                Constant.TYPE_VIDEOS -> {
                    setListItemData(MediaStoreUtils.getChildVideoFromPath(context, folderPath))
                }
                Constant.TYPE_FILE -> {
                    setListItemData(
                        MediaStoreUtils.getChildFileFromPath(
                            context, folderPath, fileType!!
                        )
                    )
                }
            }

        }
    }

    fun insertFileToRoom(fileVaultItem: FileVaultItem){
        viewModelScope.launch {
            repo.insertFileVault(fileVaultItem)
        }
    }

    fun copyMoveFile(
        context: Context,
        listFile: MutableList<File>,
        destination: File,
        targetName:MutableList<String>,
        progress: (state: Int, value: Float, currentFile: File?) -> Unit,
        onSuccess: () -> Unit,
        onError: (t: Throwable) -> Unit,
        encryptMode: Int = EncryptionMode.HIDDEN,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.copyEncrypt(
                context,
                listFile,
                destination,
                targetName,
                0L,
                progress,
                true,
                onSuccess,
                onError,
                encryptionMode = encryptMode
            )
        }
    }
}