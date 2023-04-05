package com.neko.hiepdph.calculatorvault.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.neko.hiepdph.calculatorvault.data.database.entity.HistoryEntity
import com.neko.hiepdph.calculatorvault.data.database.model.HistoryModel

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHistory(historyEntity: HistoryEntity)

    @Update
    fun updateHistory(historyEntity: HistoryEntity)


    @Query("delete from history where id=:id")
    fun deleteHistory(id: Int)

    @Query("delete from history")
    fun deleteAllHistory()

    @Query("select * from history order by time asc")
    fun getListHistoryAsc(): LiveData<List<HistoryModel>>

    @Query("select * from history order by time desc")
    fun getListHistoryDesc(): LiveData<List<HistoryModel>>


    @Query("select * from history order by id desc")
    fun getListHistory(): LiveData<List<HistoryModel>>

    @Query("select * from history where condition in (:value) order by time desc ")
    fun filterListHistory(value: List<Int>): LiveData<List<HistoryModel>>

    @Query("delete from history where id in (:listId) ")
    fun deleteHistory(listId:List<Int>)
}