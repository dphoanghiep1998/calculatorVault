package com.neko.hiepdph.calculatorvault.di


import android.content.Context
import androidx.room.Room
import com.neko.hiepdph.calculatorvault.common.Constant
import com.neko.hiepdph.calculatorvault.data.database.AppDatabase
import com.neko.hiepdph.calculatorvault.data.database.dao.BookmarkDao
import com.neko.hiepdph.calculatorvault.data.database.dao.FileVaultItemDao
import com.neko.hiepdph.calculatorvault.data.database.dao.NoteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, Constant.APP_DB).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideBookmarkDao(appDatabase: AppDatabase): BookmarkDao {
        return appDatabase.bookmarkDao
    }
    @Provides
    fun provideNoteDao(appDatabase: AppDatabase): NoteDao {
        return appDatabase.noteDao
    }

    @Provides
    fun provideFileVaultItemDao(appDatabase: AppDatabase): FileVaultItemDao {
        return appDatabase.fileVaultItemDao
    }







}