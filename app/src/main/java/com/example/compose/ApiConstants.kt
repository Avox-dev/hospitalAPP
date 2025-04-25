// ApiConstants.kt
package com.example.compose.data

object ApiConstants {
    // 기본 API URL
    const val BASE_URL = "http://192.168.219.189:5002/api"

    // 회원 관련 API
    const val REGISTER_URL = "$BASE_URL/users/register"
    const val LOGIN_URL = "$BASE_URL/users/login"
    const val LOGOUT_URL = "$BASE_URL/users/logout"
    const val USER_UPDATE_URL = "$BASE_URL/users/update"
    const val CHANGEPWD_URL = "$BASE_URL/users/change-password"
    const val WITHDRAW_URL = "$BASE_URL/users/withdraw"

    // 병원 검색 관련 API
    const val HOSPITAL_SEARCH_URL = "$BASE_URL/hospitals/search"

    // 예약 관련 API
    const val RESERVATION_URL = "$BASE_URL/reservations"
    //예약 조회 API
    const val RESERVATION_SEARCH_URL = "$BASE_URL/reservations/user"

    // 커뮤니티 관련 API
    const val POSTS_URL = "$BASE_URL/qna"
    const val NOTICES_URL = "$BASE_URL/notices"

    // 챗봇 API URL
    const val CHATBOT_URL = "$BASE_URL/ai"

    // API 요청 타임아웃 설정 (초 단위)
    const val CONNECTION_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}