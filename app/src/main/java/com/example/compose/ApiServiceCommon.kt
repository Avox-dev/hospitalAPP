// ApiServiceCommon.kt
package com.example.compose.data

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * API 요청 결과를 나타내는 sealed class
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int? = null, val message: String) : ApiResult<Nothing>()
}

/**
 * API 서비스 공통 유틸리티 클래스
 */
object ApiServiceCommon {

    // OkHttp 클라이언트 인스턴스
    private val client = OkHttpClient.Builder()
        .connectTimeout(ApiConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(ApiConstants.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(ApiConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()

    /**
     * POST 요청 수행
     * @param url API 엔드포인트 URL
     * @param jsonBody JSONObject 형태의 요청 본문
     * @return API 응답 결과
     */
    suspend fun postRequest(url: String, jsonBody: JSONObject): ApiResult<JSONObject> {
        return try {
            val requestBody = jsonBody.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            executeRequest(request)
        } catch (e: Exception) {
            ApiResult.Error(message = "네트워크 오류: ${e.message}")
        }
    }

    /**
     * GET 요청 수행
     * @param url API 엔드포인트 URL
     * @return API 응답 결과
     */
    suspend fun getRequest(url: String): ApiResult<JSONObject> {
        return try {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            executeRequest(request)
        } catch (e: Exception) {
            ApiResult.Error(message = "네트워크 오류: ${e.message}")
        }
    }

    /**
     * 요청 실행 및 응답 처리
     */
    private fun executeRequest(request: Request): ApiResult<JSONObject> {
        return try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: "{}"
                val jsonResponse = JSONObject(body)

                if (response.isSuccessful) {
                    ApiResult.Success(jsonResponse)
                } else {
                    val errorMessage = jsonResponse.optString("message", "오류 발생: ${response.code}")
                    ApiResult.Error(response.code, errorMessage)
                }
            }
        } catch (e: Exception) {
            ApiResult.Error(message = "응답 처리 오류: ${e.message}")
        }
    }
}