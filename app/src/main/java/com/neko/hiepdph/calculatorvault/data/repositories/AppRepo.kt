package com.neko.hiepdph.calculatorvault.data.repositories

import androidx.lifecycle.LiveData
import com.neko.hiepdph.calculatorvault.data.database.model.BookmarkModel
import com.neko.hiepdph.calculatorvault.data.model.NoteModel
import com.neko.hiepdph.calculatorvault.data.services.BookmarkService
import com.neko.hiepdph.calculatorvault.data.services.NoteLocalService
import com.neko.hiepdph.calculatorvault.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppRepo @Inject constructor(
    private val noteLocalService: NoteLocalService,
    private val bookmarkService: BookmarkService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

    fun getAllBookmark(): LiveData<List<BookmarkModel>> = bookmarkService.bookmarkDao.getListBookmark()

    suspend fun deleteBookmark(id:Int) = withContext(dispatcher){
        bookmarkService.bookmarkDao.deleteBookmark(id)
    }
    suspend fun insertBookmark(bookmarkModel: BookmarkModel) = withContext(dispatcher){
        bookmarkService.bookmarkDao.insertBookmark(bookmarkModel.toBookmarkEntity())
    }
    fun getAllNote(): LiveData<List<NoteModel>> = noteLocalService.noteDao.getListNote()

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
