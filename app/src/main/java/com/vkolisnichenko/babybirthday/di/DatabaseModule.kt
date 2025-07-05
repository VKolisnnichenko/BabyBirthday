package com.vkolisnichenko.babybirthday.di

import android.content.Context
import androidx.room.Room
import com.vkolisnichenko.babybirthday.data.database.AppDatabase
import com.vkolisnichenko.babybirthday.data.database.dao.BabyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideBabyDao(database: AppDatabase): BabyDao {
        return database.babyDao()
    }
}