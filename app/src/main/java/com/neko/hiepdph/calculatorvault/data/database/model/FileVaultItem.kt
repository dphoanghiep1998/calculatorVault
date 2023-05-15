package com.neko.hiepdph.calculatorvault.data.database.model

import com.neko.hiepdph.calculatorvault.data.database.entity.FileVaultItemEntity

class FileVaultItem(
    var id: Int,
    var path: String,
    var originalPath: String,
    var name: String = "",
    var encryptedName: String = "",
    var size: Long = 0L,
    var modified: Long = 0L,
    var mediaStoreId: Long = 0L,
    var ratioPicture: String = "0x0",
    var artist: String = "",
    var durationLength: Int = 0,
    var encryptionType: Int = 1,
) {
    fun toFileVaultEntity(): FileVaultItemEntity {
        val fileId = if (id == -1) 0 else id
        return FileVaultItemEntity(
            fileId,
            path,
            originalPath,
            name,
            encryptedName,
            size,
            modified,
            mediaStoreId,
            ratioPicture,
            artist,
            durationLength,
            encryptionType
        )
    }

    fun FileVaultItemEntity.toFileVaultModel(): FileVaultItem {
        return FileVaultItem(
            id,
            path,
            originalPath,
            name,
            encryptedName,
            size,
            modified,
            mediaStoreId,
            ratioPicture,
            artist,
            durationLength,
            encryptionType
        )
    }
}