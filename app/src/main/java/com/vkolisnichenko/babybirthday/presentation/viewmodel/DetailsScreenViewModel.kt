package com.vkolisnichenko.babybirthday.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkolisnichenko.babybirthday.domain.model.BabyInfo
import com.vkolisnichenko.babybirthday.domain.usecase.GetBabyInfoUseCase
import com.vkolisnichenko.babybirthday.domain.usecase.SaveBabyInfoUseCase
import com.vkolisnichenko.babybirthday.presentation.state.DetailsScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DetailsScreenViewModel @Inject constructor(
    private val getBabyInfoUseCase: GetBabyInfoUseCase,
    private val saveBabyInfoUseCase: SaveBabyInfoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DetailsScreenState())
    val state: StateFlow<DetailsScreenState> = _state.asStateFlow()

    init {
        loadBabyInfo()
    }

    private fun loadBabyInfo() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, errorMessage = null)

            getBabyInfoUseCase()
                .catch { error ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Failed to load baby info: ${error.message}"
                    )
                }
                .collect { babyInfoList ->
                    babyInfoList.takeIf { it.isNotEmpty() }?.first()?.let { babyInfo ->
                        _state.value = _state.value.copy(
                            isLoading = false,
                            name = babyInfo.name,
                            birthday = babyInfo.birthday,
                            photoPath = babyInfo.photoPath,
                            errorMessage = null
                        )
                    } ?: run {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun updateName(name: String) {
        val limitedName = if (name.length > MAX_NAME_LENGTH) name.take(MAX_NAME_LENGTH) else name

        _state.value = _state.value.copy(name = limitedName)
        saveBabyInfoIfValid()
    }

    fun updateBirthday(birthday: LocalDate) {
        _state.value = _state.value.copy(birthday = birthday)
        saveBabyInfoIfValid()
    }

    fun updatePhotoPath(photoPath: String?) {
        _state.value = _state.value.copy(photoPath = photoPath)
        saveBabyInfoIfValid()
    }

    private fun saveBabyInfoIfValid() {
        val currentState = _state.value
        if (currentState.isFormValid && !currentState.isSaving) {
            saveBabyInfo()
        }
    }

    private fun saveBabyInfo() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, errorMessage = null)

            try {
                val currentState = _state.value
                val babyInfo = BabyInfo(
                    name = currentState.name,
                    birthday = currentState.birthday ?: LocalDate.now(),
                    photoPath = currentState.photoPath
                )

                saveBabyInfoUseCase(babyInfo)

                _state.value = _state.value.copy(isSaving = false)
            } catch (error: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false, errorMessage = "Failed to save baby info: ${error.message}"
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    companion object {
        const val MAX_NAME_LENGTH = 100
    }
}