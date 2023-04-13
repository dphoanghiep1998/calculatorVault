package com.neko.hiepdph.calculatorvault.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark")
class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = -1, var name: String, var url: String,var imageIcon: String,var persistent :Boolean
) {

}