package com.neko.hiepdph.calculatorvault.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.share_preference.AppSharePreference
import com.neko.hiepdph.calculatorvault.common.utils.FileUtils
import com.neko.hiepdph.calculatorvault.common.utils.ICreateFile
import com.neko.hiepdph.calculatorvault.data.database.model.HistoryModel
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

    fun deleteSelectedHistory(listId: List<Int>) {
        viewModelScope.launch {
            appRepo.deleteSelectedHistory(listId)
        }
    }


    fun insertHistory(historyModel: HistoryModel) {
        viewModelScope.launch {
            appRepo.insertHistoryModel(historyModel)
        }
    }

    fun updateHistory(historyModel: HistoryModel) {
        viewModelScope.launch {
            appRepo.updateHistoryModel(historyModel)
        }
    }


    fun filterHistory(value: List<Int>): LiveData<List<HistoryModel>> {
        return appRepo.filterHistory(value)
    }

    fun getHistoryList(): LiveData<List<HistoryModel>> {
        return appRepo.getAllHistory()
    }


}