package com.vkolisnichenko.babybirthday.data.file

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.vkolisnichenko.babybirthday.domain.repository.FileManager
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManagerImpl @Inject constructor(
    private val context: Context
) : FileManager {

    companion object {
        private const val IMAGES_DIR = "baby_images"
        private const val TEMP_DIR = "temp_images"
        private const val IMAGE_EXTENSION = ".jpg"
        private const val AUTHORITY_SUFFIX = ".fileprovider"
    }

    private fun getImagesDirectory(): File {
        val imagesDir = File(context.filesDir, IMAGES_DIR)
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        return imagesDir
    }

    private fun getTempDirectory(): File {
        val tempDir = File(context.cacheDir, TEMP_DIR)
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return tempDir
    }

    private fun generateUniqueImageFileName(): String {
        return "baby_image_${UUID.randomUUID()}$IMAGE_EXTENSION"
    }

    override fun createImageFile(): File {
        val fileName = generateUniqueImageFileName()
        return File(getImagesDirectory(), fileName)
    }

    override fun createTempCameraFile(): File {
        val fileName = "temp_camera_${System.currentTimeMillis()}$IMAGE_EXTENSION"
        return File(context.externalCacheDir, fileName)
    }

    override fun getFileProviderUri(file: File): Uri {
        val authority = "${context.packageName}$AUTHORITY_SUFFIX"
        return FileProvider.getUriForFile(context, authority, file)
    }

    override fun isFileValid(filePath: String?): Boolean {
        if (filePath.isNullOrBlank()) return false
        val file = File(filePath)
        return file.exists() && file.canRead() && file.length() > 0
    }

    override fun deleteFile(filePath: String?): Boolean {
        if (filePath.isNullOrBlank()) return true
        val file = File(filePath)
        return if (file.exists()) {
            try {
                file.delete()
            } catch (e: SecurityException) {
                false
            }
        } else {
            true
        }
    }

    override fun cleanupTempFiles(maxAgeMillis: Long) {
        val tempDir = getTempDirectory()
        if (!tempDir.exists()) return

        val currentTime = System.currentTimeMillis()
        tempDir.listFiles()?.forEach { file ->
            if (currentTime - file.lastModified() > maxAgeMillis) {
                try {
                    file.delete()
                } catch (e: SecurityException) { }
            }
        }
    }

    override fun getFileSize(filePath: String?): Long {
        if (filePath.isNullOrBlank()) return -1
        val file = File(filePath)
        return if (file.exists()) file.length() else -1
    }
}