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


    @Query("SELECT * FROM file_vault WHERE encryptedPath = :name order by id desc")
    fun getFileByEncryptedName(name: String): FileVaultItem?

    @Query("delete from file_vault where id=:id")
    fun deleteFile(id: Int)

    @Query("delete from file_vault")
    fun deleteAllFile()

    @Query("select * from file_vault order by id desc")
    fun getListFile(): List<FileVaultItem>

    @Query("select * from file_vault where encryptedPath like :folderPath || '%'  and isDeleted = 0 order by modified desc")
    fun getListFileEncrypted(folderPath:String): LiveData<MutableList<FileVaultItem>>

    @Query("select * from file_vault where recyclerPath like :folderPath || '%' and isDeleted = 1 order by modified desc")
    fun getListFileDeleted(folderPath:String): LiveData<MutableList<FileVaultItem>>

    @Query("delete from file_vault where id in (:listId) ")
    fun deleteFile(listId:List<Int>)

    @Query("delete from file_vault where id = (SELECT MAX (id) from file_vault)")
    fun deleteLastRow()
}