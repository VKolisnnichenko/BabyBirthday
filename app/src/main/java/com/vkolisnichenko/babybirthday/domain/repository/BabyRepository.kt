package com.vkolisnichenko.babybirthday.domain.repository

import com.vkolisnichenko.babybirthday.domain.model.BabyInfo
import kotlinx.coroutines.flow.Flow

interface BabyRepository {
    fun getBabyInfo(): Flow<List<BabyInfo>>
    suspend fun saveBabyInfo(babyInfo: BabyInfo)
    suspend fun deleteBabyInfo()
}