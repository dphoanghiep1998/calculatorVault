package com.neko.hiepdph.calculatorvault.data.repositories

import androidx.lifecycle.LiveData
import com.neko.hiepdph.calculatorvault.data.database.model.HistoryModel
import com.neko.hiepdph.calculatorvault.data.model.NoteModel
import com.neko.hiepdph.calculatorvault.data.services.HistoryLocalService
import com.neko.hiepdph.calculatorvault.data.services.NoteLocalService
import com.neko.hiepdph.calculatorvault.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepo @Inject constructor(
    private val historyLocalService: HistoryLocalService,
    private val noteLocalService: NoteLocalService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun insertHistoryModel(model: HistoryModel) = withContext(dispatcher) {
        historyLocalService.historyDao.insertHistory(model.toHistoryEntity())
    }

    suspend fun updateHistoryModel(model: HistoryModel) = withContext(dispatcher) {
        historyLocalService.historyDao.updateHistory(model.toHistoryEntity())
    }

    suspend fun deleteAllHistory() = withContext(dispatcher) {
        historyLocalService.historyDao.deleteAllHistory()
    }

    suspend fun deleteHistory(id: Int) = withContext(dispatcher) {
        historyLocalService.historyDao.deleteHistory(id)
    }

    suspend fun deleteSelectedHistory(listId: List<Int>) = withContext(dispatcher) {
        historyLocalService.historyDao.deleteHistory(listId)
    }




    fun getAllHistoryDesc(): LiveData<List<HistoryModel>> =
        historyLocalService.historyDao.getListHistoryDesc()

    fun getAllHistoryAsc(): LiveData<List<HistoryModel>> =
        historyLocalService.historyDao.getListHistoryAsc()

    fun getAllHistory(): LiveData<List<HistoryModel>> =
        historyLocalService.historyDao.getListHistory()


    fun filterHistory(value: List<Int>): LiveData<List<HistoryModel>> =
        historyLocalService.historyDao.filterListHistory(value)

    fun getAllNote(): LiveData<List<NoteModel>> =
        noteLocalService.noteDao.getListNote()
    suspend fun insertNoteModel(model: NoteModel) = withContext(dispatcher) {
        noteLocalService.noteDao.insertNote(model.toNoteEntity())
    }

    suspend fun updateNoteModel(model: NoteModel) = withContext(dispatcher) {
        noteLocalService.noteDao.updateNote(model.toNoteEntity())
    }

    suspend fun deleteAllNote() = withContext(dispatcher) {
        noteLocalService.noteDao.deleteAllNote()
    }

    suspend fun deleteNote(id: Int) = withContext(dispatcher) {
        noteLocalService.noteDao.deleteNote(id)
    }

    suspend fun deleteSelectedNote(listId: List<Int>) = withContext(dispatcher) {
        noteLocalService.noteDao.deleteNote(listId)
    }


}
