package com.vkolisnichenko.babybirthday.data.repository

import com.vkolisnichenko.babybirthday.data.database.dao.BabyDao
import com.vkolisnichenko.babybirthday.data.mapper.toDomainList
import com.vkolisnichenko.babybirthday.data.mapper.toEntity
import com.vkolisnichenko.babybirthday.domain.model.BabyInfo
import com.vkolisnichenko.babybirthday.domain.repository.BabyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BabyRepositoryImpl @Inject constructor(
    private val babyDao: BabyDao
) : BabyRepository {

    override fun getBabyInfo(): Flow<List<BabyInfo>> {
        return babyDao.getBabyInfo().map { entities ->
            entities.toDomainList()
        }
    }

    override suspend fun saveBabyInfo(babyInfo: BabyInfo) {
        babyDao.saveBabyInfo(babyInfo.toEntity())
    }

    override suspend fun deleteBabyInfo() {
        babyDao.deleteBabyInfo()
    }
}