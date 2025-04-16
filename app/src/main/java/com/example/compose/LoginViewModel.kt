// LoginViewModel.kt
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.compose.data.User
import com.example.compose.data.UserRepository

class LoginViewModel : ViewModel() {
    private val userRepository = UserRepository.getInstance()

    // 로그인 상태를 관리하는 StateFlow
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // 로그인 함수
    fun login(userId: String, password: String) {
        viewModelScope.launch {
            try {
                // 로딩 상태로 변경
                _loginState.value = LoginState.Loading

                // 로딩 시간 시뮬레이션 (실제 구현에서는 제거)
                delay(1000)

                // 여기서 실제 DB 쿼리나 API 호출이 이루어져야 합니다
                // 예시로 간단한 검증만 수행합니다
                val isSuccess = validateCredentials(userId, password)

                if (isSuccess) {
                    // 사용자 정보 저장 (api서버로 저장해야하나?)
                    userRepository.setCurrentUser(User(userId = userId))
                    // 로그인 성공
                    _loginState.value = LoginState.Success("로그인에 성공했습니다!")
                } else {
                    // 로그인 실패
                    _loginState.value = LoginState.Error("아이디 또는 비밀번호가 일치하지 않습니다.")
                }
            } catch (e: Exception) {
                // 예외 발생
                _loginState.value = LoginState.Error("로그인 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    // 임시 검증 함수 (실제로는 DB나 API를 통해 검증해야 함)
    private suspend fun validateCredentials(userId: String, password: String): Boolean {
        // 실제 구현에서는 여기서 DB 쿼리나 API 호출을 통해 자격 증명을 검증합니다
        // 예시로 간단한 테스트 계정만 체크합니다
        return userId == "test" && password == "1234"
    }

    // 로그인 상태를 나타내는 sealed class
    sealed class LoginState {
        object Initial : LoginState()
        object Loading : LoginState()
        data class Success(val message: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}