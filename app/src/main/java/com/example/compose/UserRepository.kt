// UserRepository.kt
package com.example.compose.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    private var sessionId: String? = null
    fun setSessionId(id: String) {
        sessionId = id
    }

    fun getSessionId(): String? {
        return sessionId
    }
    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    fun logoutUser() {
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean {
        return currentUser.value != null
    }

    companion object {
        // 싱글톤 패턴 구현
        private var instance: UserRepository? = null

        fun getInstance(): UserRepository {
            if (instance == null) {
                instance = UserRepository()
            }
            return instance!!
        }
    }
}

// User 데이터 클래스
data class User(
    val userId: String,
    val userName: String = "사용자", // 기본값 설정
    val email: String = "",        // 추가
    val phone: String = "",        // 추가
    val birthdate: String = "",    // 추가
    val address: String = ""       // 추가
)