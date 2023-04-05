package com.neko.hiepdph.calculatorvault.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neko.hiepdph.calculatorvault.common.utils.SelfCleaningLiveData
import com.neko.hiepdph.calculatorvault.data.model.GroupItem
import com.neko.hiepdph.calculatorvault.data.model.NoteModel
import com.neko.hiepdph.calculatorvault.data.repositories.AppRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val appRepo: AppRepo
) :ViewModel() {


    fun getAllNote():LiveData<List<NoteModel>>{
        return appRepo.getAllNote()
    }

    fun insertNewNote(model: NoteModel){
        viewModelScope.launch {
            appRepo.insertNoteModel(model)
        }
    }
    fun deleteNote(listId:List<Int>){
        viewModelScope.launch {
            appRepo.deleteSelectedNote(listId)
        }
    }
}