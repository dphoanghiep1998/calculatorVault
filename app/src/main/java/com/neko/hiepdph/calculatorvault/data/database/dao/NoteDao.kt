package com.neko.hiepdph.calculatorvault.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neko.hiepdph.calculatorvault.data.database.entity.NoteEntity
import com.neko.hiepdph.calculatorvault.data.model.NoteModel

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(noteEntity: NoteEntity)

    @Update
    fun updateNote(noteEntity: NoteEntity)


    @Query("delete from note where id=:id")
    fun deleteNote(id: Int)

    @Query("delete from note")
    fun deleteAllNote()


    @Query("select * from note order by id desc")
    fun getListNote(): LiveData<List<NoteModel>>

    @Query("delete from note where id in (:listId) ")
    fun deleteNote(listId:List<Int>)
}