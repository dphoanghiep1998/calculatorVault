package com.neko.hiepdph.calculatorvault.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.common.utils.ICreateFile
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

    fun createFolder(parentDir: File, fileName: String,callback:ICreateFile) {
        viewModelScope.launch(Dispatchers.IO) {
            FileUtils.createSecretFile(parentDir, fileName,callback)
        }
    }



}