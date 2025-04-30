package com.android.hospitalAPP.data

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import okhttp3.ConnectionPool
import okhttp3.Protocol
import java.io.IOException
import java.net.Proxy
import com.android.hospitalAPP.util.AesEncryptionUtil

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int? = null, val message: String) : ApiResult<Nothing>()
}

object ApiServiceCommon {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // 타임아웃 시간 줄임
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .connectionPool(ConnectionPool(0, 1, TimeUnit.MINUTES))  // Keep-alive 연결 사용 안함
        .protocols(listOf(Protocol.HTTP_1_1))  // HTTP/1.1만 사용
        .proxy(Proxy.NO_PROXY)  // 프록시 무시
        .build()

    suspend fun postRequest(
        url: String,
        jsonBody: JSONObject,
        useEncryption: Boolean = false  // 기본값은 false (암호화 사용하지 않음)
    ): ApiResult<JSONObject> {
        return try {
            val sessionId = UserRepository.getInstance().getSessionId()
            Log.d("ApiServiceCommon", "세션 아이디 값 확인: $sessionId")

            jsonBody.put("session", sessionId)

            Log.d("ApiServiceCommon", "POST 요청 URL: $url")
            Log.d("ApiServiceCommon", "POST 요청 Body: $jsonBody")

            // 요청 빌더 초기화
            val requestBuilder = Request.Builder().url(url)

            // 암호화 사용 여부에 따라 요청 본문 및 헤더 설정
            if (useEncryption) {
                // 암호화 사용 시 - JSON 문자열로 변환 후 AES-256 암호화
                Log.d("ApiServiceCommon", "암호화 사용: JSON 데이터를 암호화합니다")
                val jsonString = jsonBody.toString()
                val encryptedData = AesEncryptionUtil.encryptAesBase64(jsonString)

                Log.d("ApiServiceCommon", "암호화된 데이터: $encryptedData")

                // 암호화된 문자열을 본문으로 사용
                val requestBody = encryptedData.toRequestBody("text/plain".toMediaTypeOrNull())

                // POST 요청 설정 (암호화)
                requestBuilder
                    .post(requestBody)
                    .addHeader("X-Encrypted", "true")  // 암호화 사용 표시
                    .addHeader("Content-Type", "text/plain")  // Content-Type 변경
            } else {
                // 암호화 사용하지 않을 때 - 일반 JSON 요청
                Log.d("ApiServiceCommon", "암호화 미사용: 일반 JSON으로 전송")

                // JSON 요청 본문 생성
                val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaTypeOrNull())

                // POST 요청 설정 (암호화 없음)
                requestBuilder
                    .post(requestBody)
                    .addHeader("Content-Type", "application/json")
            }

            // 공통 헤더 설정
            requestBuilder
                .addHeader("Cookie", "session=$sessionId")
                .addHeader("Connection", "close")

            // 요청 생성 및 실행
            val request = requestBuilder.build()
            executeRequest(request)
        } catch (e: Exception) {
            Log.e("ApiServiceCommon", "POST 요청 중 예외 발생: ${e.message}", e)
            ApiResult.Error(message = "네트워크 오류: ${e.message}")
        }
    }

    suspend fun getRequest(url: String): ApiResult<JSONObject> {
        return try {
            val sessionId = UserRepository.getInstance().getSessionId()
            Log.d("ApiServiceCommon", "GET 요청 URL: $url")

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Cookie", "session=$sessionId")
                .addHeader("Connection", "close")  // 서버와 일치하도록 close 설정
                .build()

            executeRequest(request)
        } catch (e: Exception) {
            Log.e("ApiServiceCommon", "GET 요청 중 예외 발생: ${e.message}", e)
            ApiResult.Error(message = "네트워크 오류: ${e.message}")
        }
    }

    private fun executeRequest(request: Request): ApiResult<JSONObject> {
        var responseBody = "{}"

        return try {
            val response = client.newCall(request).execute()

            try {
                Log.d("ApiServiceCommon", "응답 코드: ${response.code}")
                Log.d("ApiServiceCommon", "응답 헤더:")
                response.headers.forEach { (name, value) ->
                    Log.d("ApiServiceCommon", "  $name: $value")
                }

                responseBody = response.body?.use { it.string() } ?: "{}"
                Log.d("ApiServiceCommon", "원본 응답: $responseBody")

                // 🔐 조건부 복호화: 응답 헤더가 X-Encrypted: true 인 경우만
                val isEncrypted = response.header("X-Encrypted")?.equals("true", ignoreCase = true) == true
                if (isEncrypted) {
                    val decrypted = AesEncryptionUtil.decryptAesBase64(
                        encryptedBase64 = responseBody,
                        key = AesEncryptionUtil.SECRET_KEY,
                        iv = AesEncryptionUtil.IV
                    )
                    responseBody = decrypted
                    Log.d("ApiServiceCommon", "복호화된 응답: $decrypted")
                }

            } catch (e: IOException) {
                Log.e("ApiServiceCommon", "응답 본문 읽기 실패: ${e.message}", e)
                if (responseBody == "{}") {
                    responseBody = "{\"message\":\"응답 본문 읽기 실패\"}"
                }
            } finally {
                response.close()
            }

            val jsonResponse = try {
                JSONObject(responseBody)
            } catch (e: Exception) {
                Log.e("ApiServiceCommon", "JSON 파싱 실패: ${e.message}", e)
                JSONObject().put("message", "JSON 파싱 실패: ${e.message}")
            }

            if (response.isSuccessful) {
                ApiResult.Success(jsonResponse)
            } else {
                val errorMessage = jsonResponse.optString("message", "오류 발생: ${response.code}")
                Log.e("ApiServiceCommon", "에러 응답: $errorMessage")
                ApiResult.Error(response.code, errorMessage)
            }

        } catch (e: Exception) {
            Log.e("ApiServiceCommon", "응답 처리 중 예외 발생: ${e.message}", e)
            ApiResult.Error(message = "응답 처리 오류: ${e.message}")
        }
    }


}