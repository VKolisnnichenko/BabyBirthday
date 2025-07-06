package com.vkolisnichenko.babybirthday.domain.usecase

import android.net.Uri
import com.vkolisnichenko.babybirthday.domain.repository.FileManager
import java.io.File
import javax.inject.Inject

class PrepareCameraUseCase @Inject constructor(
    private val fileManager: FileManager
) {

    data class CameraSetup(
        val tempFile: File,
        val uri: Uri
    )

    operator fun invoke(): CameraSetup {
        val tempFile = fileManager.createTempCameraFile()
        val uri = fileManager.getFileProviderUri(tempFile)
        return CameraSetup(tempFile, uri)
    }
}