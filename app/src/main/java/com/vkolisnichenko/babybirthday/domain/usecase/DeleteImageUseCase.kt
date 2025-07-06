package com.vkolisnichenko.babybirthday.domain.usecase

import com.vkolisnichenko.babybirthday.domain.repository.FileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DeleteImageUseCase @Inject constructor(
    private val fileManager: FileManager
) {

    suspend operator fun invoke(imagePath: String?): Boolean = withContext(Dispatchers.IO) {
        return@withContext fileManager.deleteFile(imagePath)
    }
}