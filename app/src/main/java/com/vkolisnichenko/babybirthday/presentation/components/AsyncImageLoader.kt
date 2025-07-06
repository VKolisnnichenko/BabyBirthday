package com.vkolisnichenko.babybirthday.presentation.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vkolisnichenko.babybirthday.presentation.theme.BabyBirthdayAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun AsyncImageLoader(
    imagePath: String?,
    fallbackPainter: Painter? = null,
    contentDescription: String? = null,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    isCircular: Boolean = false
) {
    var imageState by remember(imagePath) { mutableStateOf<ImageLoadState>(ImageLoadState.Loading) }

    val imageModifier = if (isCircular) {
        modifier.clip(CircleShape)
    } else {
        modifier
    }

    LaunchedEffect(imagePath) {
        if (imagePath.isNullOrBlank()) {
            imageState = ImageLoadState.Empty
            return@LaunchedEffect
        }

        imageState = ImageLoadState.Loading

        try {
            val bitmap = withContext(Dispatchers.IO) {
                val file = File(imagePath)
                if (file.exists()) {
                    BitmapFactory.decodeFile(file.absolutePath)
                } else {
                    null
                }
            }

            imageState = if (bitmap != null) {
                ImageLoadState.Success(bitmap.asImageBitmap())
            } else {
                ImageLoadState.Error
            }
        } catch (e: Exception) {
            imageState = ImageLoadState.Error
        }
    }

    when (val state = imageState) {
        is ImageLoadState.Loading -> {
            LoadingContent(modifier = imageModifier)
        }

        is ImageLoadState.Error -> {
            ErrorContent(
                fallbackPainter = fallbackPainter,
                contentDescription = contentDescription,
                modifier = imageModifier,
                contentScale = contentScale
            )
        }

        is ImageLoadState.Success -> {
            Image(
                bitmap = state.bitmap,
                contentDescription = contentDescription,
                modifier = imageModifier,
                contentScale = contentScale
            )
        }

        is ImageLoadState.Empty -> {
            PlaceholderContent(
                fallbackPainter = fallbackPainter,
                contentDescription = contentDescription,
                modifier = imageModifier,
                contentScale = contentScale
            )
        }
    }
}

private sealed class ImageLoadState {
    object Loading : ImageLoadState()
    object Error : ImageLoadState()
    object Empty : ImageLoadState()
    data class Success(val bitmap: ImageBitmap) : ImageLoadState()
}

@Composable
private fun PlaceholderContent(
    fallbackPainter: Painter?,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    if (fallbackPainter != null) {
        Image(
            painter = fallbackPainter,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier
) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ErrorContent(
    fallbackPainter: Painter?,
    contentDescription: String?,
    modifier: Modifier,
    contentScale: ContentScale
) {
    if (fallbackPainter != null) {
        Image(
            painter = fallbackPainter,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    } else {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.errorContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Error loading image",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AsyncImageLoaderPreview() {
    BabyBirthdayAppTheme {
        AsyncImageLoader(
            imagePath = null,
            modifier = Modifier.size(100.dp),
            isCircular = true
        )
    }
}