package com.neko.hiepdph.calculatorvault.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "file_vault")
class FileVaultItemEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var originalPath: String,
    var encryptedPath: String = "",
    var recyclerPath: String,
    var name: String = "",
    var size: Long = 0L,
    var modified: Long = 0L,
    var timeLock: Long = 0L,
    var mediaStoreId: Long = 0L,
    var ratioPicture: String? = null,
    var artist: String = "",
    var durationLength: Int = 0,
    var encryptionType: Int = 1,
    var fileType: String = "",
    var fileRealType: String? = null,
    var thumb: ByteArray? = null
) {}