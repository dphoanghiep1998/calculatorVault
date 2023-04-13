package com.neko.hiepdph.calculatorvault.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.neko.hiepdph.calculatorvault.data.converter.Converter
import com.neko.hiepdph.calculatorvault.data.database.dao.BookmarkDao
import com.neko.hiepdph.calculatorvault.data.database.dao.NoteDao
import com.neko.hiepdph.calculatorvault.data.database.entity.BookmarkEntity
import com.neko.hiepdph.calculatorvault.data.database.entity.NoteEntity


@Database(entities = [NoteEntity::class, BookmarkEntity::class], version = 1)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao
    abstract val bookmarkDao: BookmarkDao
}
