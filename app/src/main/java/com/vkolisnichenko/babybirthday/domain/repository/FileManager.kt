package com.vkolisnichenko.babybirthday.domain.repository

import android.net.Uri
import java.io.File

interface FileManager {
    fun createImageFile(): File
    fun createTempCameraFile(): File
    fun getFileProviderUri(file: File): Uri
    fun isFileValid(filePath: String?): Boolean
    fun deleteFile(filePath: String?): Boolean
    fun cleanupTempFiles(maxAgeMillis: Long = 3600000)
    fun getFileSize(filePath: String?): Long
}