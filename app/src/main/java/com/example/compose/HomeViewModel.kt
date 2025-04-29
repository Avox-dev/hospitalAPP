// HomeViewModel.kt
package com.example.compose.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.compose.data.UserRepository
import com.example.compose.util.SharedPreferencesManager

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "HomeViewModel"
    private val userRepository = UserRepository.getInstance()
    private val prefsManager = SharedPreferencesManager.getInstance(application)

    // 로그인 ViewModel 인스턴스 생성
    private val loginViewModel = LoginViewModel(application)

    // 자동 로그인 정보 존재 여부를 나타내는 StateFlow
    private val _hasAutoLoginInfo = MutableStateFlow(checkHasAutoLoginInfo())
    val hasAutoLoginInfo: StateFlow<Boolean> = _hasAutoLoginInfo.asStateFlow()

    init {
        Log.d(TAG, "HomeViewModel 초기화")
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
     * 자동 로그인 실행
     * @return 자동 로그인 정보가 있어 실행했으면 true, 없어서 실행하지 않았으면 false
     */
    fun executeAutoLogin(): Boolean {
        return if (_hasAutoLoginInfo.value) {
            Log.d(TAG, "자동 로그인 실행")
            loginViewModel.executeAutoLogin()
            true
        } else {
            Log.d(TAG, "자동 로그인 정보 없음")
            false
        }
    }

    /**
     * 로그아웃 실행
     */
    fun logout() {
        viewModelScope.launch {
            Log.d(TAG, "로그아웃 실행")
            userRepository.logoutUser()

            // 자동 로그인 설정 해제 및 정보 클리어
            prefsManager.saveLoginInfo("", "", false)
            prefsManager.clearAll()

            // 상태 업데이트
            updateHasAutoLoginInfo()
        }
    }
}