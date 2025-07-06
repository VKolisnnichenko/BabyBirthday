package com.vkolisnichenko.babybirthday.domain.repository

import android.net.Uri
import java.io.File

interface ImageProcessor {
    suspend fun processAndSaveImage(sourceUri: Uri, targetFile: File): Boolean
    suspend fun getImageDimensions(uri: Uri): Pair<Int, Int>?
    suspend fun isImageSizeAcceptable(uri: Uri): Boolean
}