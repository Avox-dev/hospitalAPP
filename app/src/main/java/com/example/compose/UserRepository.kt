// UserRepository.kt
package com.example.compose.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    private var sessionId: String? = null

    // UserService 인스턴스
    private val userService = UserService()

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
        // 서버에 로그아웃 요청 보내기
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // UserService의 logout 메서드 호출
                val result = userService.logout()

                when (result) {
                    is ApiResult.Success -> {
                        // 로그아웃 성공 시 로컬 상태 초기화
                        _currentUser.value = null
                        sessionId = null
                    }
                    is ApiResult.Error -> {
                        // 서버 로그아웃 실패 시에도 로컬에서는 로그아웃 처리
                        _currentUser.value = null
                        sessionId = null
                        println("로그아웃 API 오류: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                // 네트워크 오류 등의 예외 발생 시에도 로컬에서 로그아웃 처리
                _currentUser.value = null
                sessionId = null
                println("로그아웃 처리 중 예외 발생: ${e.message}")
            }
        }
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

// User 데이터 클래스는 변경하지 않음
data class User(
    val userId: String,
    val userName: String = "사용자", // 기본값 설정
    val email: String = "",        // 추가
    val phone: String = "",        // 추가
    val birthdate: String = "",    // 추가
    val address: String = "",      // 추가
    val sessionId: String = "",    // 추가(세션 아이디)
    val address_detail: String = ""// 추가(상세주소)
)

