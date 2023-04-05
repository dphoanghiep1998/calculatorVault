package com.neko.hiepdph.calculatorvault.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = -1,
    var title: String,
    var content: String,
    var date: Long,


) {

}