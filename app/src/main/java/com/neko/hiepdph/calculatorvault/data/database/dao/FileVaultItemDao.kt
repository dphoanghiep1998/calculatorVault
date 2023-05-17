package com.neko.hiepdph.calculatorvault.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neko.hiepdph.calculatorvault.data.database.entity.FileVaultItemEntity
import com.neko.hiepdph.calculatorvault.data.database.entity.NoteEntity
import com.neko.hiepdph.calculatorvault.data.database.model.FileVaultItem
import com.neko.hiepdph.calculatorvault.data.model.NoteModel

@Dao
interface FileVaultItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(fileVaultItem: FileVaultItemEntity)

    @Update
    fun updateFile(fileVaultItem: FileVaultItemEntity)


    @Query("SELECT * FROM file_vault WHERE encryptedPath = :name")
    fun getFileByEncryptedName(name: String): FileVaultItem?

    @Query("delete from file_vault where id=:id")
    fun deleteFile(id: Int)

    @Query("delete from file_vault")
    fun deleteAllFile()

    @Query("select * from file_vault")
    fun getListFile(): List<FileVaultItem>

    @Query("select * from file_vault where encryptedPath like :folderPath || '%'  and isDeleted = 0")
    fun getListFileEncrypted(folderPath:String): LiveData<List<FileVaultItem>>

    @Query("select * from file_vault where recyclerPath like :folderPath || '%' and isDeleted = 1")
    fun getListFileDeleted(folderPath:String): LiveData<List<FileVaultItem>>

    @Query("delete from file_vault where id in (:listId) ")
    fun deleteFile(listId:List<Int>)
}