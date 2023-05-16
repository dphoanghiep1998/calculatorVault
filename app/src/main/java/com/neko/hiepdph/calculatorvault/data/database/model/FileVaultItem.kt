package com.neko.hiepdph.calculatorvault.data.database.model

import com.neko.hiepdph.calculatorvault.data.database.entity.FileVaultItemEntity


class FileVaultItem(
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
) {
    fun toFileVaultEntity(): FileVaultItemEntity {
        val fileId = if (id == -1) 0 else id
        return FileVaultItemEntity(
            fileId,
            originalPath,
            encryptedPath,
            recyclerPath,
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
            thumb
        )
    }



    fun FileVaultItemEntity.toFileVaultModel(): FileVaultItem {
        return FileVaultItem(
            id,
            originalPath,
            encryptedPath,
            recyclerPath,
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
            thumb
        )
    }
}