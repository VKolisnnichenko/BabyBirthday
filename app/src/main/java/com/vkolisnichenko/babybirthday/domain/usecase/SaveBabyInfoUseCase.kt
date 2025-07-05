package com.vkolisnichenko.babybirthday.domain.usecase

import com.vkolisnichenko.babybirthday.domain.model.BabyInfo
import com.vkolisnichenko.babybirthday.domain.repository.BabyRepository
import javax.inject.Inject

class SaveBabyInfoUseCase @Inject constructor(
    private val repository: BabyRepository
) {
    suspend operator fun invoke(babyInfo: BabyInfo) {
        repository.saveBabyInfo(babyInfo)
    }
}