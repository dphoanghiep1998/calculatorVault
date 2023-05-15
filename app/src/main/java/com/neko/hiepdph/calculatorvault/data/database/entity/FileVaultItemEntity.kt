package com.neko.hiepdph.calculatorvault.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_vault")
class FileVaultItemEntity(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var path: String,
    val originalPath: String,
    val name: String = "",
    val encryptedName: String = "",
    var size: Long = 0L,
    var modified: Long = 0L,
    var mediaStoreId: Long = 0L,
    var ratioPicture: String = "0x0",
    var artist: String = "",
    var durationLength: Int = 0,
    var encryptionType: Int = 1

) {}