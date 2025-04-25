package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import com.example.compose.data.ApiResult
import com.example.compose.data.UserRepository
import com.example.compose.data.UserService
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope

class WithdrawAccountViewModel : ViewModel() {
    private val userService = UserService()

    private val _withdrawResult = mutableStateOf<ApiResult<Unit>?>(null)
    val withdrawResult: State<ApiResult<Unit>?> = _withdrawResult

    fun withdrawAccount(currentPassword: String) {
        viewModelScope.launch {
            val result = userService.withdrawAccount(currentPassword)

            when (result) {
                is ApiResult.Success -> {
                    val currentUser = UserRepository.getInstance().currentUser.value
                    if (currentUser != null) {
                        userService.logout()
                        val updatedUser = currentUser.copy(
                            userId = "",
                            userName = "사용자",
                            email = "",
                            phone = "",
                            birthdate = "",
                            address = "",
                            address_detail = "",
                            sessionId = ""
                        )
                        UserRepository.getInstance().setCurrentUser(updatedUser)
                    }
                    _withdrawResult.value = ApiResult.Success(Unit)
                }
                is ApiResult.Error -> {
                    _withdrawResult.value = result
                }
            }
        }
    }
}