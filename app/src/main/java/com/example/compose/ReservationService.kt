// ReservationService.kt
package com.example.compose.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * 병원 예약 관련 API 요청을 처리하는 서비스 클래스
 */
class ReservationService {

    /**
     * 병원 예약 API 요청
     * @param name 예약자 이름
     * @param phone 연락처
     * @param hospital 병원명
     * @param address 병원 주소
     * @param message 예약 메시지/증상
     * @param email 이메일 (선택)
     * @return 예약 처리 결과
     */
    suspend fun makeReservation(
        name: String,
        phone: String,
        hospital: String,
        address: String,
        message: String,
        email: String? = null
    ): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        // JSON 요청 본문 생성
        val jsonBody = JSONObject().apply {
            put("name", name)
            put("phone", phone)
            put("hospital", hospital)
            put("address", address)
            put("message", message)
            if (!email.isNullOrBlank()) {
                put("email", email)
            }
        }

        // API 요청 실행
        ApiServiceCommon.postRequest(ApiConstants.RESERVATION_URL, jsonBody)
    }

    /**
     * 예약 내역 조회 API 요청
     * @param userId 사용자 ID
     * @return 예약 내역 목록
     */
    suspend fun getReservations(userId: String): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        // JSON 요청 본문 생성
        val jsonBody = JSONObject().apply {
            put("userId", userId)
        }

        // API 요청 실행
        ApiServiceCommon.postRequest("${ApiConstants.RESERVATION_URL}/list", jsonBody)
    }
}