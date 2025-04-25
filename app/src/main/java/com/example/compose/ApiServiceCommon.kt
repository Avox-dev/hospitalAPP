package com.example.compose.data

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import com.example.compose.data.UserRepository
import okhttp3.ConnectionPool
import okhttp3.Protocol
import java.io.IOException

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
        .build()

    suspend fun postRequest(url: String, jsonBody: JSONObject): ApiResult<JSONObject> {
        return try {
            val sessionId = UserRepository.getInstance().getSessionId()
            Log.d("ApiServiceCommon", "세션 아이디 값 확인: $sessionId")

            jsonBody.put("session", sessionId)

            Log.d("ApiServiceCommon", "POST 요청 URL: $url")
            Log.d("ApiServiceCommon", "POST 요청 Body: $jsonBody")

            val requestBody = jsonBody.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Cookie", "session=$sessionId")
                .addHeader("Connection", "close")  // 서버와 일치하도록 close 설정
                .build()

            executeRequest(request)
        } catch (e: Exception) {
            Log.e("ApiServiceCommon", "POST 요청 중 예외 발생: ${e.message}", e)
            ApiResult.Error(message = "네트워크 오류: ${e.message}")
        }
    }

    suspend fun getRequest(url: String): ApiResult<JSONObject> {
        return try {
            Log.d("ApiServiceCommon", "GET 요청 URL: $url")

            val request = Request.Builder()
                .url(url)
                .get()
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
                // 응답 헤더 로깅
                Log.d("ApiServiceCommon", "응답 코드: ${response.code}")
                Log.d("ApiServiceCommon", "응답 헤더:")
                response.headers.forEach { (name, value) ->
                    Log.d("ApiServiceCommon", "  $name: $value")
                }

                // 응답 본문 안전하게 읽기
                responseBody = response.body?.string() ?: "{}"
                Log.d("ApiServiceCommon", "응답 본문: $responseBody")

            } catch (e: IOException) {
                // 응답 본문 읽기 실패 - 이미 연결이 닫혔을 수 있음
                Log.e("ApiServiceCommon", "응답 본문 읽기 실패: ${e.message}", e)
                if (responseBody == "{}") {
                    // 본문을 아직 못 읽었다면 기본값 설정
                    responseBody = "{\"message\":\"응답 본문 읽기 실패\"}"
                }
            } finally {
                // 모든 경우에 response를 닫기
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