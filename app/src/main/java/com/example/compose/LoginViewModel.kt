// LoginViewModel.kt
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.compose.data.User
import com.example.compose.data.UserRepository
import com.example.compose.data.ApiResult
import com.example.compose.data.UserService
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val userRepository = UserRepository.getInstance()
    private val userService = UserService()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun login(userId: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val maxRetries = 3
            var retryCount = 0
            var lastException: Exception? = null

            while (retryCount < maxRetries) {
                try {
                    val jsonBody = JSONObject().apply {
                        put("userId", userId)
                        put("password", password)
                    }



                    val result = withContext(Dispatchers.IO) {
                        userService.login(userId, password)
                    }

                    when (result) {
                        is ApiResult.Success -> {
                            val responseData = result.data


                            val status = responseData.optString("status")
                            val message = responseData.optString("message")

                            if (status == "success") {
                                val userData = responseData.optJSONObject("data")
                                val sessionId = responseData.optString("session", "")
                                if (userData != null) {
                                    val username = userData.optString("username")
                                    val email = userData.optString("email")
                                    val phone = userData.optString("phone", "")
                                    val birthdate = userData.optString("birthdate", "")
                                    val address = userData.optString("address", "")

                                    userRepository.setCurrentUser(
                                        User(
                                            userId = username,
                                            userName = username,
                                            email = email,
                                            phone = phone,
                                            birthdate = birthdate,
                                            address = address
                                        )
                                    )
                                    userRepository.setSessionId(sessionId)

                                    _loginState.value = LoginState.Success(message)
                                } else {
                                    _loginState.value = LoginState.Error("사용자 정보를 불러오는데 실패했습니다.")
                                }
                            } else {
                                _loginState.value = LoginState.Error(message)
                            }
                            return@launch
                        }
                        is ApiResult.Error -> {

                            lastException = Exception(result.message)
                        }
                    }
                } catch (e: Exception) {

                    lastException = e
                }

                retryCount++
                if (retryCount < maxRetries) {
                    val waitTime = 1000L * retryCount

                    delay(waitTime)
                }
            }

            // 모든 재시도 실패 후
            _loginState.value = LoginState.Error("로그인 중 오류가 발생했습니다: ${lastException?.message ?: "알 수 없는 오류"}")
        }
    }

    sealed class LoginState {
        object Initial : LoginState()
        object Loading : LoginState()
        data class Success(val message: String) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}