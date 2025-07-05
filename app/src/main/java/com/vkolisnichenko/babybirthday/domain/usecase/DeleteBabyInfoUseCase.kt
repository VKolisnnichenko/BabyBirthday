package com.vkolisnichenko.babybirthday.domain.usecase

import com.vkolisnichenko.babybirthday.domain.repository.BabyRepository
import javax.inject.Inject

class DeleteBabyInfoUseCase @Inject constructor(
    private val repository: BabyRepository
) {
    suspend operator fun invoke() {
        return repository.deleteBabyInfo()
    }
}