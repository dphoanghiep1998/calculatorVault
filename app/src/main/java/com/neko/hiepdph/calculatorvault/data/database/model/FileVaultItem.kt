package com.neko.hiepdph.calculatorvault.data.database.model

import android.graphics.Bitmap
import com.neko.hiepdph.calculatorvault.data.database.entity.FileVaultItemEntity


data class FileVaultItem(
    var id: Int,
    var originalPath: String,
    var encryptedPath: String = "",
    var recyclerPath: String = "",
    var decodePath: String = "",
    var name: String = "",
    var size: Long = 0L,
    var modified: Long = 0L,
    var timeLock: Long = 0L,
    var mediaStoreId: Long = 0L,
    var ratioPicture: String? = null,
    var artist: String? = "",
    var durationLength: Int? = 0,
    var encryptionType: Int = 1,
    var fileType: String = "",
    var fileRealType: String? = null,
    var isDeleted: Boolean = false,
    var thumb: Bitmap? = null
) {
    fun toFileVaultEntity(): FileVaultItemEntity {
        val fileId = if (id == -1) 0 else id
        return FileVaultItemEntity(
            fileId,
            originalPath,
            encryptedPath,
            recyclerPath,
            decodePath,
            name,
            size,
            modified,
            timeLock,
            mediaStoreId,
            ratioPicture,
            artist,
            durationLength,
            encryptionType,
            fileType,
            fileRealType,
            isDeleted,
            thumb
        )
    }

    fun FileVaultItemEntity.toFileVaultModel(): FileVaultItem {
        return FileVaultItem(
            id,
            originalPath,
            encryptedPath,
            recyclerPath,
            decodePath,
            name,
            size,
            modified,
            timeLock,
            mediaStoreId,
            ratioPicture,
            artist,
            durationLength,
            encryptionType,
            fileType,
            fileRealType,
            isDeleted,
            thumb
        )
    }
}