package com.vkolisnichenko.babybirthday.di

import com.vkolisnichenko.babybirthday.data.database.dao.BabyDao
import com.vkolisnichenko.babybirthday.data.repository.BabyRepositoryImpl
import com.vkolisnichenko.babybirthday.domain.repository.BabyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}