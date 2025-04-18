// ApiConstants.kt
package com.example.compose.data

object ApiConstants {
    // 기본 API URL
    private const val BASE_URL = "http://192.168.219.91:5002/api"

    // 회원 관련 API
    const val REGISTER_URL = "$BASE_URL/register"
    const val LOGIN_URL = "$BASE_URL/login"
    const val USER_UPDATE_URL = "$BASE_URL/users/update"

    // 병원 검색 관련 API
    const val HOSPITAL_SEARCH_URL = "$BASE_URL/hospitals/search"

    // 예약 관련 API
    const val RESERVATION_URL = "$BASE_URL/reservations"

    // 커뮤니티 관련 API
    const val POSTS_URL = "$BASE_URL/posts"
    const val NOTICES_URL = "$BASE_URL/notices"

    // API 요청 타임아웃 설정 (초 단위)
    const val CONNECTION_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L
}