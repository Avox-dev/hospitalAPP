package com.android.hospitalAPP.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.hospitalAPP.data.ApiResult
import com.android.hospitalAPP.data.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf

class HealthInfoInputViewModel : ViewModel() {
    private val userService = UserService()

    val bloodType = mutableStateOf("")
    val heightCm = mutableStateOf("")
    val weightKg = mutableStateOf("")
    val allergyInfo = mutableStateOf("")
    val pastIllnesses = mutableStateOf("")
    val chronicDiseases = mutableStateOf("")

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Initial)
    val updateUiState: StateFlow<UpdateState> = _updateState

    fun submitHealthInfo() {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            try {
                val result = userService.submitHealthInfo(
                    bloodType.value,
                    heightCm.value,
                    weightKg.value,
                    allergyInfo.value,
                    pastIllnesses.value,
                    chronicDiseases.value
                )

                when (result) {
                    is ApiResult.Success -> _updateState.value = UpdateState.Success("건강 정보가 저장되었습니다.")
                    is ApiResult.Error -> _updateState.value = UpdateState.Error(result.message)
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error("제출 실패: ${e.message}")
            }
        }
    }
    fun loadHealthInfo() {
        viewModelScope.launch {
            try {
                val result = userService.getHealthInfo()
                if (result is ApiResult.Success) {
                    val data = result.data.optJSONObject("data")
                    data?.let {
                        bloodType.value = it.optString("blood_type", "")
                        heightCm.value = it.optString("height_cm", "")
                        weightKg.value = it.optString("weight_kg", "")
                        allergyInfo.value = it.optString("allergy_info", "")
                        pastIllnesses.value = it.optString("past_illnesses", "")
                        chronicDiseases.value = it.optString("chronic_diseases", "")
                    }
                }
            } catch (e: Exception) {
                println("건강 정보 로딩 실패: ${e.message}")
            }
        }
    }

    sealed class UpdateState {
        object Initial : UpdateState()
        object Loading : UpdateState()
        data class Success(val message: String) : UpdateState()
        data class Error(val message: String) : UpdateState()
    }
}