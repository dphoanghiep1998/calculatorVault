package com.neko.hiepdph.calculatorvault.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = -1,
    var time: Long,
    var status: Int,
    var glycemicIndexMol: Float = 0f,
    var glycemicIndexMg: Float = 0f,
    var condition: Int,
    var notes: MutableList<String> = mutableListOf()

) {

}