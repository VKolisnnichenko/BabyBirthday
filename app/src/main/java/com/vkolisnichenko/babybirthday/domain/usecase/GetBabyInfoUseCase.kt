package com.vkolisnichenko.babybirthday.domain.usecase

import com.vkolisnichenko.babybirthday.domain.model.BabyInfo
import com.vkolisnichenko.babybirthday.domain.repository.BabyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBabyInfoUseCase @Inject constructor(
    private val repository: BabyRepository
) {
    operator fun invoke(): Flow<List<BabyInfo>> {
        return repository.getBabyInfo()
    }
}