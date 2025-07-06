package com.vkolisnichenko.babybirthday.di

import android.content.Context
import com.vkolisnichenko.babybirthday.data.database.dao.BabyDao
import com.vkolisnichenko.babybirthday.data.file.FileManagerImpl
import com.vkolisnichenko.babybirthday.data.image.ImageProcessorImpl
import com.vkolisnichenko.babybirthday.data.repository.BabyRepositoryImpl
import com.vkolisnichenko.babybirthday.domain.repository.BabyRepository
import com.vkolisnichenko.babybirthday.domain.repository.FileManager
import com.vkolisnichenko.babybirthday.domain.repository.ImageProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBabyRepository(babyDao: BabyDao): BabyRepository {
        return BabyRepositoryImpl(babyDao)
    }

    @Provides
    @Singleton
    fun provideFileManager(
        @ApplicationContext context: Context
    ): FileManager {
        return FileManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideImageProcessor(
        @ApplicationContext context: Context
    ): ImageProcessor {
        return ImageProcessorImpl(context)
    }
}