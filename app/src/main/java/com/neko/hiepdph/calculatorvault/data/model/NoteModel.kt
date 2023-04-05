package com.neko.hiepdph.calculatorvault.data.model

import com.neko.hiepdph.calculatorvault.data.database.entity.HistoryEntity
import com.neko.hiepdph.calculatorvault.data.database.entity.NoteEntity
import com.neko.hiepdph.calculatorvault.data.database.model.HistoryModel

data class NoteModel(var id: Int = -1, val title: String, val content: String, val date: Long) {
    fun toNoteEntity(): NoteEntity {
        val historyId = if (id == -1) 0 else id
        return NoteEntity(
            id = historyId,
            title,
            content, date
        )
    }

    fun NoteEntity.toNoteModel(): NoteModel {
        return NoteModel(
            id, title, content, date
        )
    }
}