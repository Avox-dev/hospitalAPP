package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.data.ApiResult
import com.example.compose.data.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ProfileManagementViewModel : ViewModel() {

    private val userService = UserService()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Initial)
    val updateUiState: StateFlow<UpdateState> = _updateState

    fun update(email: String, phone: String, birthdate: String, address: String, address_detail: String) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            try {
                val result = userService.updateUserInfo(email, phone, birthdate, address, address_detail)
                when (result) {
                    is ApiResult.Success -> {
                        val responseData = result.data
                        val status = responseData.optString("status")
                        val message = responseData.optString("message")

                        _updateState.value =
                            if (status == "success") UpdateState.Success(message)
                            else UpdateState.Error(message)
                    }

                    is ApiResult.Error -> {
                        _updateState.value = UpdateState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _updateState.value = UpdateState.Error("정보 수정 중 오류 발생: ${e.message}")
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