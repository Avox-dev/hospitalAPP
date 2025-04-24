// LoginViewModel.kt
package com.example.compose.viewmodel

import android.content.Context
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
import android.content.SharedPreferences
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
                    val result = withContext(Dispatchers.IO) {
                        userService.login(userId, password)
                    }

                    when (result) {
                        is ApiResult.Success -> {
                            val responseData = result.data
                            val status = responseData.optString("status")
                            val message = responseData.optString("message")

                            val sessionId = responseData.optString("session", null)

                            if (status == "success" && sessionId != null) {
                                val userData = responseData.optJSONObject("data")

                                if (userData != null) {
                                    val id = userData.optInt("id", -1).toString()
                                    val username = userData.optString("username", "")
                                    val email = userData.optString("email", "")
                                    val phone = userData.optString("phone", "")
                                    val birthdate = userData.optString("birthdate", "")
                                    val address = userData.optString("address", "")
                                    val address_detail = userData.optString("address_detail", "")
                                    userRepository.setCurrentUser(
                                        User(
                                            userId = id,
                                            userName = username,
                                            email = email,
                                            phone = phone,
                                            birthdate = birthdate,
                                            address = address,
                                            sessionId = sessionId,
                                            address_detail = address_detail
                                        )
                                    )
                                    userRepository.setSessionId(sessionId)
                                    _loginState.value = LoginState.Success(message)
                                } else {
                                    _loginState.value = LoginState.Error("사용자 정보를 불러오는데 실패했습니다.")
                                }
                            } else if (sessionId != null) {
                                _loginState.value = LoginState.Error(message)
                            } else {
                                _loginState.value = LoginState.Error("세션 정보를 불러오는데 실패했습니다")
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
                    delay(1000L * retryCount)
                }
            }

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