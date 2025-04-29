// LoginViewModel.kt
package com.example.compose.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
import com.example.compose.util.SharedPreferencesManager

// ✅ 로그인 처리를 담당하는 ViewModel (Application 컨텍스트를 사용하기 위해 AndroidViewModel 상속)
class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "LoginViewModel"
    private val userRepository = UserRepository.getInstance() // 유저 정보 저장소
    private val userService = UserService() // 로그인 API 호출용
    private val prefsManager = SharedPreferencesManager.getInstance(application) // SharedPreferences 관리자

    // 로그인 상태를 관리하는 StateFlow
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    // 사용자 ID, 비밀번호, 자동 로그인 상태를 관리하는 StateFlow
    private val _userId = MutableStateFlow(prefsManager.getUserId())
    val userId: StateFlow<String> = _userId.asStateFlow()

    private val _password = MutableStateFlow(prefsManager.getPassword())
    val password: StateFlow<String> = _password.asStateFlow()

    private val _rememberMe = MutableStateFlow(prefsManager.isAutoLoginEnabled())
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()

    // 자동 로그인 정보 존재 여부를 나타내는 StateFlow
    private val _hasAutoLoginInfo = MutableStateFlow(checkHasAutoLoginInfo())
    val hasAutoLoginInfo: StateFlow<Boolean> = _hasAutoLoginInfo.asStateFlow()

    init {
        Log.d(TAG, "LoginViewModel 초기화")
        // 여기서는, 로그인 함수를 직접 호출하지 않고 자동 로그인 정보만 확인
        updateHasAutoLoginInfo()
    }

    /**
     * 자동 로그인 정보가 있는지 확인하고 상태 업데이트
     */
    private fun updateHasAutoLoginInfo() {
        val hasInfo = checkHasAutoLoginInfo()
        _hasAutoLoginInfo.value = hasInfo
        Log.d(TAG, "자동 로그인 정보 존재 여부: $hasInfo")
    }

    /**
     * 자동 로그인 정보가 있는지 확인
     */
    private fun checkHasAutoLoginInfo(): Boolean {
        val isEnabled = prefsManager.isAutoLoginEnabled()
        val hasUserId = prefsManager.getUserId().isNotEmpty()
        val hasPassword = prefsManager.getPassword().isNotEmpty()
        return isEnabled && hasUserId && hasPassword
    }

    /**
     * 자동 로그인 실행 (외부에서 호출 가능)
     */
    fun executeAutoLogin(): Boolean {
        if (_hasAutoLoginInfo.value) {
            val savedUserId = prefsManager.getUserId()
            val savedPassword = prefsManager.getPassword()
            Log.d(TAG, "자동 로그인 실행 - ID: $savedUserId")
            login(savedUserId, savedPassword, true)
            return true
        }
        Log.d(TAG, "자동 로그인 정보 없음")
        return false
    }

    /**
     * 로그인 상태 값 업데이트
     */
    fun updateUserId(userId: String) {
        _userId.value = userId
    }

    fun updatePassword(password: String) {
        _password.value = password
    }

    fun updateRememberMe(rememberMe: Boolean) {
        _rememberMe.value = rememberMe
    }

    /**
     * ✅ 로그인 처리
     * @param userId 사용자 ID
     * @param password 비밀번호
     * @param isAutoLogin 자동 로그인 여부 (기본값: rememberMe.value)
     */
    fun login(
        userId: String = _userId.value,
        password: String = _password.value,
        isAutoLogin: Boolean = _rememberMe.value
    ) {
        viewModelScope.launch {
            Log.d(TAG, "로그인 시도 - ID: $userId, 자동 로그인: $isAutoLogin")
            _loginState.value = LoginState.Loading // 로딩 상태 표시

            val maxRetries = 3 // 최대 재시도 횟수
            var retryCount = 0
            var lastException: Exception? = null

            // 재시도 로직
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
                                // 사용자 정보 파싱
                                val userData = responseData.optJSONObject("data")

                                if (userData != null) {
                                    val id = userData.optInt("id", -1).toString()
                                    val username = userData.optString("username", "")
                                    val email = userData.optString("email", "")
                                    val phone = userData.optString("phone", "")
                                    val birthdate = userData.optString("birthdate", "")
                                    val address = userData.optString("address", "")
                                    val address_detail = userData.optString("address_detail", "")

                                    // 자동 로그인 정보 저장
                                    if (isAutoLogin) {
                                        Log.d(TAG, "자동 로그인 정보 저장 - ID: $userId")
                                        prefsManager.saveLoginInfo(userId, password, true)
                                    } else {
                                        Log.d(TAG, "자동 로그인 비활성화로 정보 삭제")
                                        prefsManager.clearLoginInfo() // 자동 로그인 비활성화 시 정보 삭제
                                    }

                                    // 세션 ID 항상 저장
                                    prefsManager.saveSessionId(sessionId)

                                    // ✅ UserRepository에 로그인 사용자 정보 저장
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

                                    // 자동 로그인 정보 상태 업데이트
                                    updateHasAutoLoginInfo()

                                    _loginState.value = LoginState.Success(message)
                                } else {
                                    _loginState.value = LoginState.Error("사용자 정보를 불러오는데 실패했습니다.")
                                }
                            } else if (sessionId != null) {
                                _loginState.value = LoginState.Error(message)
                            } else {
                                _loginState.value = LoginState.Error("세션 정보를 불러오는데 실패했습니다")
                            }
                            return@launch // 성공했으면 바로 종료
                        }

                        is ApiResult.Error -> {
                            // 서버에서 에러 반환
                            lastException = Exception(result.message)
                        }
                    }
                } catch (e: Exception) {
                    // 네트워크 오류 등 예외 발생
                    lastException = e
                }

                // 실패했을 때 재시도 로직
                retryCount++
                if (retryCount < maxRetries) {
                    delay(1000L * retryCount) // 점점 대기시간 늘려서 재시도
                }
            }

            // 최종 실패 처리
            _loginState.value = LoginState.Error("로그인 중 오류가 발생했습니다: ${lastException?.message ?: "알 수 없는 오류"}")
        }
    }

    /**
     * 로그아웃 처리
     */
    fun logout() {
        viewModelScope.launch {
            userRepository.logoutUser()

            // 자동 로그인 설정 해제 및 정보 클리어
            prefsManager.saveLoginInfo("", "", false)
            prefsManager.clearAll()

            // 로그아웃 후 상태 초기화
            _userId.value = ""
            _password.value = ""
            _rememberMe.value = false
            _hasAutoLoginInfo.value = false
        }
    }

    /**
     * ✅ 로그인 상태를 표현하는 sealed class
     */
    sealed class LoginState {
        object Initial : LoginState() // 초기 상태
        object Loading : LoginState() // 로딩 중
        data class Success(val message: String) : LoginState() // 성공 (메시지 포함)
        data class Error(val message: String) : LoginState() // 실패 (에러 메시지 포함)
    }
}