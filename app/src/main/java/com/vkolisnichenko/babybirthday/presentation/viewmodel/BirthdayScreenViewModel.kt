package com.vkolisnichenko.babybirthday.presentation.viewmodel

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkolisnichenko.babybirthday.domain.model.BirthdayScreenVariant
import com.vkolisnichenko.babybirthday.domain.model.ImageSource
import com.vkolisnichenko.babybirthday.domain.usecase.CalculateAgeUseCase
import com.vkolisnichenko.babybirthday.domain.usecase.CaptureScreenshotUseCase
import com.vkolisnichenko.babybirthday.domain.usecase.CreateShareIntentUseCase
import com.vkolisnichenko.babybirthday.domain.usecase.GetBabyInfoUseCase
import com.vkolisnichenko.babybirthday.domain.usecase.PrepareCameraUseCase
import com.vkolisnichenko.babybirthday.domain.usecase.SaveBabyInfoUseCase
import com.vkolisnichenko.babybirthday.domain.usecase.SaveScreenshotUseCase
import com.vkolisnichenko.babybirthday.domain.usecase.SelectImageUseCase
import com.vkolisnichenko.babybirthday.domain.usecase.ValidateImageUseCase
import com.vkolisnichenko.babybirthday.presentation.state.BirthdayScreenState
import com.vkolisnichenko.babybirthday.presentation.state.ImagePickerState
import com.vkolisnichenko.babybirthday.presentation.state.ShareState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class BirthdayScreenViewModel @Inject constructor(
    private val getBabyInfoUseCase: GetBabyInfoUseCase,
    private val saveBabyInfoUseCase: SaveBabyInfoUseCase,
    private val calculateAgeUseCase: CalculateAgeUseCase,
    private val selectImageUseCase: SelectImageUseCase,
    private val validateImageUseCase: ValidateImageUseCase,
    private val prepareCameraUseCase: PrepareCameraUseCase,
    private val captureScreenshotUseCase: CaptureScreenshotUseCase,
    private val saveScreenshotUseCase: SaveScreenshotUseCase,
    private val createShareIntentUseCase: CreateShareIntentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BirthdayScreenState())
    val state: StateFlow<BirthdayScreenState> = _state.asStateFlow()

    private val _imagePickerState = MutableStateFlow(ImagePickerState())
    val imagePickerState: StateFlow<ImagePickerState> = _imagePickerState.asStateFlow()

    private val _shareState = MutableStateFlow(ShareState())
    val shareState: StateFlow<ShareState> = _shareState.asStateFlow()

    init {
        selectRandomUiVariant()
        fetchBabyData()
    }

    private fun selectRandomUiVariant() {
        val randomVariant = BirthdayScreenVariant.entries.toTypedArray().random()
        _state.value = _state.value.copy(variant = randomVariant)
    }

    private fun fetchBabyData() {
        viewModelScope.launch {
            getBabyInfoUseCase()
                .collect { babyInfoList ->
                    if (babyInfoList.isNotEmpty()) {
                        babyInfoList.first().let { babyInfo ->
                            _state.value = _state.value.copy(
                                babyName = babyInfo.name,
                                ageInfo = calculateAgeUseCase(babyInfo.birthday),
                                photoPath = babyInfo.photoPath ?: ""
                            )
                        }
                    }
                }
        }
    }

    fun prepareCameraCapture(): PrepareCameraUseCase.CameraSetup {
        return prepareCameraUseCase()
    }

    fun onImageSelected(uri: Uri, source: ImageSource) {
        viewModelScope.launch {
            _imagePickerState.value = _imagePickerState.value.copy(
                isProcessingImage = true,
                errorMessage = null
            )

            val validationResult = validateImageUseCase(uri)
            if (validationResult is ValidateImageUseCase.ValidationResult.Invalid) {
                _imagePickerState.value = _imagePickerState.value.copy(
                    isProcessingImage = false,
                    errorMessage = validationResult.reason
                )
                return@launch
            }

            val currentState = _state.value
            val result = selectImageUseCase(uri, currentState.photoPath)

            when (result) {
                is SelectImageUseCase.Result.Success -> {
                    updatePhotoPath(result.savedImagePath)
                    _imagePickerState.value = _imagePickerState.value.copy(
                        isProcessingImage = false,
                        errorMessage = null
                    )
                }

                is SelectImageUseCase.Result.Error -> {
                    _imagePickerState.value = _imagePickerState.value.copy(
                        isProcessingImage = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun onCameraImageCaptured(success: Boolean, tempFile: File?) {
        if (success && tempFile != null && tempFile.exists() && tempFile.length() > 0) {
            val uri = Uri.fromFile(tempFile)
            onImageSelected(uri, ImageSource.CAMERA)
        }
    }

    fun clearImagePickerError() {
        _imagePickerState.value = _imagePickerState.value.copy(errorMessage = null)
    }

    fun onShareClick(view: View): Intent? {
        viewModelScope.launch {
            _shareState.value = _shareState.value.copy(
                isSharing = true,
                hideUIForScreenshot = true,
                errorMessage = null
            )

            delay(100)

            val screenshotResult = captureScreenshotUseCase(view)

            when (screenshotResult) {
                is CaptureScreenshotUseCase.Result.Success -> {
                    val saveResult = saveScreenshotUseCase(screenshotResult.bitmap)

                    when (saveResult) {
                        is SaveScreenshotUseCase.Result.Success -> {
                            _shareState.value = _shareState.value.copy(
                                isSharing = false,
                                hideUIForScreenshot = false
                            )
                        }
                        is SaveScreenshotUseCase.Result.Error -> {
                            _shareState.value = _shareState.value.copy(
                                isSharing = false,
                                hideUIForScreenshot = false,
                                errorMessage = saveResult.message
                            )
                        }
                    }
                }
                is CaptureScreenshotUseCase.Result.Error -> {
                    _shareState.value = _shareState.value.copy(
                        isSharing = false,
                        hideUIForScreenshot = false,
                        errorMessage = screenshotResult.message
                    )
                }
            }
        }

        return null
    }

    fun clearShareError() {
        _shareState.value = _shareState.value.copy(errorMessage = null)
    }

    private fun updatePhotoPath(photoPath: String) {
        _state.value = _state.value.copy(photoPath = photoPath)
        saveBabyInfoWithNewPhoto(photoPath)
    }

    private fun saveBabyInfoWithNewPhoto(photoPath: String) {
        viewModelScope.launch {
            try {
                val babyInfoList = getBabyInfoUseCase().first()
                if (babyInfoList.isNotEmpty()) {
                    val babyInfo = babyInfoList.first()
                    val updatedBabyInfo = babyInfo.copy(photoPath = photoPath)
                    saveBabyInfoUseCase(updatedBabyInfo)
                }
            } catch (e: Exception) { }
        }
    }
}