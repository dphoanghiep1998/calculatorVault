package com.neko.hiepdph.calculatorvault.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neko.hiepdph.calculatorvault.data.database.entity.BookmarkEntity
import com.neko.hiepdph.calculatorvault.data.database.model.BookmarkModel

@Dao
interface BookmarkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmarkEntity: BookmarkEntity)

    @Update
    fun updateBookmark(bookmarkEntity: BookmarkEntity)


    @Query("delete from bookmark where id=:id")
    fun deleteBookmark(id: Int)

    @Query("delete from bookmark")
    fun deleteBookmark()


    @Query("select * from bookmark")
    fun getListBookmark(): LiveData<List<BookmarkModel>>

    @Query("delete from bookmark where id in (:listId) ")
    fun deleteBookmark(listId:List<Int>)
}