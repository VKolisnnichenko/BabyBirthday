package com.vkolisnichenko.babybirthday.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vkolisnichenko.babybirthday.domain.model.BirthdayScreenVariant
import com.vkolisnichenko.babybirthday.domain.usecase.CalculateAgeUseCase
import com.vkolisnichenko.babybirthday.domain.usecase.GetBabyInfoUseCase
import com.vkolisnichenko.babybirthday.presentation.state.BirthdayScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BirthdayScreenViewModel @Inject constructor(
    private val getBabyInfoUseCase: GetBabyInfoUseCase,
    private val calculateAgeUseCase: CalculateAgeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BirthdayScreenState())
    val state: StateFlow<BirthdayScreenState> = _state.asStateFlow()

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

}