package com.vkolisnichenko.babybirthday.data.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.core.graphics.scale
import androidx.exifinterface.media.ExifInterface
import com.vkolisnichenko.babybirthday.domain.repository.ImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageProcessorImpl @Inject constructor(
    private val context: Context
) : ImageProcessor {

    companion object {
        private const val MAX_IMAGE_SIZE = 1024
        private const val COMPRESSION_QUALITY = 85
        private const val MAX_FILE_SIZE_MB = 5
        private const val BYTES_IN_MB = 1024 * 1024
    }

    override suspend fun processAndSaveImage(sourceUri: Uri, targetFile: File): Boolean =
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(sourceUri)?.use { inputStream ->

                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeStream(inputStream, null, options)

                    val sampleSize = calculateInSampleSize(options, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE)

                    context.contentResolver.openInputStream(sourceUri)?.use { stream ->
                        val bitmap = decodeSampledBitmap(stream, sampleSize)
                            ?: return@withContext false

                        try {
                            val rotatedBitmap = applyExifRotation(bitmap, sourceUri)
                            val finalBitmap = resizeBitmapIfNeeded(rotatedBitmap, MAX_IMAGE_SIZE)
                            val success = saveBitmapToFile(finalBitmap, targetFile)
                            cleanupBitmaps(bitmap, rotatedBitmap, finalBitmap)
                            success
                        } catch (e: Exception) {
                            if (!bitmap.isRecycled) bitmap.recycle()
                            false
                        }
                    }
                } ?: false
            } catch (e: Exception) {
                false
            }
        }

    override suspend fun getImageDimensions(uri: Uri): Pair<Int, Int>? =
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                    }
                    BitmapFactory.decodeStream(inputStream, null, options)

                    if (options.outWidth > 0 && options.outHeight > 0) {
                        Pair(options.outWidth, options.outHeight)
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                null
            }
        }

    override suspend fun isImageSizeAcceptable(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val fileSize = when (uri.scheme) {
                "file" -> {
                    val file = File(uri.path ?: return@withContext false)
                    file.length()
                }
                "content" -> {
                    context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                        pfd.statSize
                    } ?: return@withContext false
                }
                else -> return@withContext false
            }

            val sizeMB = fileSize / BYTES_IN_MB.toFloat()
            sizeMB <= MAX_FILE_SIZE_MB * 2
        } catch (e: Exception) {
            false
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun decodeSampledBitmap(inputStream: InputStream, sampleSize: Int): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: OutOfMemoryError) {
            if (sampleSize < 8) {
                decodeSampledBitmapWithHigherSample(inputStream, sampleSize * 2)
            } else {
                null
            }
        }
    }

    private fun decodeSampledBitmapWithHigherSample(
        inputStream: InputStream,
        sampleSize: Int
    ): Bitmap? {
        return try {
            val options = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeStream(inputStream, null, options)
        } catch (e: OutOfMemoryError) {
            null
        }
    }


    private suspend fun applyExifRotation(bitmap: Bitmap, imageUri: Uri): Bitmap =
        withContext(Dispatchers.Default) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val exif = inputStream?.let { ExifInterface(it) }
                inputStream?.close()

                val orientation = exif?.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                ) ?: ExifInterface.ORIENTATION_NORMAL

                val rotationAngle = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }

                if (rotationAngle != 0f) {
                    val matrix = Matrix().apply { postRotate(rotationAngle) }
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                } else {
                    bitmap
                }
            } catch (e: Exception) {
                bitmap
            }
        }

    private fun resizeBitmapIfNeeded(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        val ratio = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return try {
            bitmap.scale(newWidth, newHeight)
        } catch (e: OutOfMemoryError) {
            bitmap
        }
    }

    private suspend fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean =
        withContext(Dispatchers.IO) {
            try {
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)
                }

                val fileSizeMB = file.length() / BYTES_IN_MB.toFloat()
                if (fileSizeMB > MAX_FILE_SIZE_MB) {
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)
                    }
                }

                true
            } catch (e: IOException) {
                false
            }
        }

    private fun cleanupBitmaps(bitmap: Bitmap, rotatedBitmap: Bitmap, finalBitmap: Bitmap) {
        if (bitmap != rotatedBitmap && !bitmap.isRecycled) bitmap.recycle()
        if (rotatedBitmap != finalBitmap && !rotatedBitmap.isRecycled) rotatedBitmap.recycle()
        if (!finalBitmap.isRecycled) finalBitmap.recycle()
    }
}