// UserService.kt
package com.android.hospitalAPP.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * 사용자 관련 API 요청을 처리하는 서비스 클래스
 */
class UserService {

    /**
     * 회원가입 API 요청
     * @param email 사용자 이메일
     * @param userId 사용자 아이디
     * @param password 비밀번호
     * @param name 이름
     * @param birthdate 생년월일
     * @param phone 전화번호
     * @param address 주소
     * @return 회원가입 처리 결과
     */
    // UserService.kt의 register 메서드 수정
    suspend fun register(
        email: String,
        userId: String,
        password: String,
        birthdate: String,
        phone: String,
        address: String,
        address_detail: String
    ): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        // JSON 요청 본문 생성
        val jsonBody = JSONObject().apply {
            put("username", userId)     // username 필드명 맞춤
            put("password", password)
            put("email", email)
            put("birthdate", birthdate)
            put("phone", phone)
            put("address", address)
            put("address_detail", address_detail)
        }

        // API 요청 실행
        ApiServiceCommon.postRequest(ApiConstants.REGISTER_URL, jsonBody)
    }

    /**
     * 로그인 API 요청
     * @param userId 사용자 아이디
     * @param password 비밀번호
     * @return 로그인 처리 결과
     */
    suspend fun login(
        userId: String,
        password: String,

    ): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        // JSON 요청 본문 생성
        val jsonBody = JSONObject().apply {
            put("username", userId)
            put("password", password)
        }

        // API 요청 실행
        ApiServiceCommon.postRequest(ApiConstants.LOGIN_URL, jsonBody)
    }

    /**
     * ✅ 사용자 정보 업데이트 API 요청
     * @param email 이메일
     * @param phone 전화번호
     * @param birthdate 생년월일
     * @param address 주소
     * @param address_detail 상세 주소
     * @return 업데이트 요청 결과 (ApiResult)
     */
    suspend fun updateUserInfo(
        email: String,
        phone: String,
        birthdate: String,
        address: String,
        address_detail: String
    ): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        // JSON 요청 본문 생성
        val jsonBody = JSONObject().apply {
            put("email", email)
            put("phone", phone)
            put("birthdate", birthdate)
            put("address", address)
            put("address_detail", address_detail)
        }

        // API 요청 실행
        ApiServiceCommon.postRequest(ApiConstants.USER_UPDATE_URL, jsonBody)
    }

    /**
     * 로그아웃 API 요청
     * @return 로그아웃 처리 결과
     */
    suspend fun logout(): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        // 로그아웃은 별도의 데이터 없이 POST 요청만 수행
        ApiServiceCommon.postRequest(ApiConstants.LOGOUT_URL, JSONObject())
    }

    /**
     * 비밀번호 변경 API 요청
     * @param current_password 기존 비밀번호
     * @param new_password 새 비밀번호
     * @return 로그인 처리 결과
     */
    suspend fun updatePwd(
        current_password: String,
        new_password: String,

        ): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        // JSON 요청 본문 생성
        val jsonBody = JSONObject().apply {
            put("current_password", current_password)
            put("new_password", new_password)
        }

        // API 요청 실행
        ApiServiceCommon.postRequest(ApiConstants.CHANGEPWD_URL, jsonBody)
    }

    /**
     * ✅ 회원 탈퇴 API 요청
     * @param password 사용자 비밀번호 (확인용)
     * @return 탈퇴 요청 결과 (ApiResult)
     */
    suspend fun withdrawAccount(
        password: String

        ): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        // JSON 요청 본문 생성
        val jsonBody = JSONObject().apply {
            put("password", password)
        }

        // API 요청 실행
        ApiServiceCommon.postRequest(ApiConstants.WITHDRAW_URL, jsonBody)
    }

}

