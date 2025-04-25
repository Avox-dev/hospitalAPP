// ProfileManagementViewModel.kt
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compose.data.ApiResult
import com.example.compose.data.UserRepository
import com.example.compose.data.UserService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileManagementViewModel : ViewModel() {

    private val userService = UserService()

    // 화면에 보여줄 업데이트 상태
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Initial)
    val updateUiState: StateFlow<UpdateState> = _updateState

    // 프로필 수정 성공 여부 플래그
    private val _isEditSuccess = MutableStateFlow(false)
    val isEditSuccess: StateFlow<Boolean> = _isEditSuccess

    /**
     * 사용자 정보 업데이트
     */
    fun update(
        email: String,
        phone: String,
        birthdate: String,
        address: String,
        address_detail: String
    ) {
        viewModelScope.launch {
            // 1) 수정 시도 전에는 항상 false로 초기화
            _isEditSuccess.value = false
            // 2) 로딩 상태 표시
            _updateState.value = UpdateState.Loading

            try {
                val result = userService.updateUserInfo(email, phone, birthdate, address, address_detail)
                when (result) {
                    is ApiResult.Success -> {
                        val responseData = result.data
                        val status = responseData.optString("status")
                        val message = responseData.optString("message")

                        if (status == "success") {

                            val currentUser = UserRepository.getInstance().currentUser.value

                            if (currentUser != null) {
                                val updatedUser = currentUser.copy(
                                    email = email,
                                    phone = phone,
                                    birthdate = birthdate,
                                    address = address,
                                    address_detail = address_detail
                                )

                                UserRepository.getInstance().setCurrentUser(updatedUser)

                            // 3) 성공 상태 표시
                            _updateState.value = UpdateState.Success(message)
                            // 4) 스크린에서 감지할 성공 플래그 true
                            _isEditSuccess.value = true
                            }

                        } else {
                            _updateState.value = UpdateState.Error(message)
                        }
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

    /**
     * 성공 플래그를 UI 로직에서 다시 쓸 수 있도록 초기화
     */
    fun clearEditSuccess() {
        _isEditSuccess.value = false
    }

    sealed class UpdateState {
        object Initial : UpdateState()
        object Loading : UpdateState()
        data class Success(val message: String) : UpdateState()
        data class Error(val message: String) : UpdateState()
    }
}
