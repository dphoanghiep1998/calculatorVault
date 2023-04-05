package com.neko.hiepdph.calculatorvault.data.database.model

import android.os.Parcelable
import com.neko.hiepdph.calculatorvault.data.database.entity.HistoryEntity
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class HistoryModel(
    var id: Int = -1,
    var time: Long = Date().time,
    var glycemicIndexMol: Float = 0f,
    var glycemicIndexMg: Float = 0f,
    var status: Int = 0,
    var condition: Int = 0,
    var notes: MutableList<String> = mutableListOf()

) : Parcelable {

    fun toHistoryEntity(): HistoryEntity {
        val historyId = if (id == -1) 0 else id
        return HistoryEntity(
            id = historyId,
            glycemicIndexMol = glycemicIndexMol,
            glycemicIndexMg = glycemicIndexMg,
            time = time,
            status = status,
            condition = condition,
            notes = notes

        )
    }

    fun HistoryEntity.toHistoryModel(): HistoryModel {
        return HistoryModel(
            id = id,
            glycemicIndexMol = glycemicIndexMol,
            glycemicIndexMg = glycemicIndexMg,
            time = time,
            status = status,
            condition = condition,
            notes = notes
        )
    }
}