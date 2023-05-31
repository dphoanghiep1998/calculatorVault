package com.neko.hiepdph.calculatorvault.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.CopyFiles
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.data.repositories.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class IntruderViewModel @Inject constructor(val appRepo: AppRepo) : ViewModel() {

    fun getItemListFromFolder(
        folderPath: String
    ): LiveData<MutableList<FileVaultItem>> {
        return appRepo.getAllFileInEnCryptFolder(folderPath)
    }

    fun copyMoveFile(
        context: Context,
        listFile: MutableList<File>,
        destination: MutableList<File>,
        progress: (value: Float, currentFile: File?) -> Unit,
        onSuccess: (MutableList<String>) -> Unit,
        onError: (t: Throwable) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            CopyFiles.copy(context, listFile, destination, 0L, progress, onSuccess, onError)
        }
    }
}