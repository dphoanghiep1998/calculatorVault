package com.neko.hiepdph.calculatorvault.data.model

import android.os.Parcelable
import com.neko.hiepdph.calculatorvault.data.database.entity.NoteEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteModel(var id: Int = -1, var title: String, var content: String, var date: Long) :
    Parcelable {
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