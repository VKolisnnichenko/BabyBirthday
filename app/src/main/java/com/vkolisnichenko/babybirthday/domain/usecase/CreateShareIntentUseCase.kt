package com.vkolisnichenko.babybirthday.domain.usecase

import android.content.Intent
import android.net.Uri
import javax.inject.Inject

class CreateShareIntentUseCase @Inject constructor() {

    operator fun invoke(imageUri: Uri): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, "Check out this birthday celebration!")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}