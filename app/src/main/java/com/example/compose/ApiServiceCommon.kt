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

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int? = null, val message: String) : ApiResult<Nothing>()
}

object ApiServiceCommon {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // 타임아웃 시간 늘림
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .connectionPool(ConnectionPool(5, 1, TimeUnit.MINUTES))
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            // Keep-Alive 헤더 추가
            val requestWithHeaders = originalRequest.newBuilder()
                .header("Connection", "keep-alive")
                .header("Keep-Alive", "timeout=60, max=1000")
                .build()

            // 타임아웃 설정
            chain.withConnectTimeout(60, TimeUnit.SECONDS)
                .withReadTimeout(60, TimeUnit.SECONDS)
                .withWriteTimeout(60, TimeUnit.SECONDS)
                .proceed(requestWithHeaders)
        }
        .protocols(listOf(Protocol.HTTP_1_1)) // HTTP/2 문제 회피를 위해 HTTP/1.1만 사용
        .build()

    suspend fun postRequest(url: String, jsonBody: JSONObject): ApiResult<JSONObject> {
        return try {
            val sessionId = UserRepository.getInstance().getSessionId()
            Log.d("ApiServiceCommon", "세션 아이디 값 확인: $sessionId")
            //세션 아이디 추가

            jsonBody.put("session", UserRepository.getInstance().getSessionId())


            Log.d("ApiServiceCommon", "POST 요청 URL: $url")
            Log.d("ApiServiceCommon", "POST 요청 Body: $jsonBody")

            val requestBody = jsonBody.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Cookie", "session=$sessionId")
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
                .build()

            executeRequest(request)
        } catch (e: Exception) {
            Log.e("ApiServiceCommon", "GET 요청 중 예외 발생: ${e.message}", e)
            ApiResult.Error(message = "네트워크 오류: ${e.message}")
        }
    }

    private fun executeRequest(request: Request): ApiResult<JSONObject> {
        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: "{}"
                Log.d("ApiServiceCommon", "응답 코드: ${response.code}")
                Log.d("ApiServiceCommon", "응답 본문: $responseBody")

                val jsonResponse = JSONObject(responseBody)

                if (response.isSuccessful) {
                    ApiResult.Success(jsonResponse)
                } else {
                    val errorMessage = jsonResponse.optString("message", "오류 발생: ${response.code}")
                    Log.e("ApiServiceCommon", "에러 응답: $errorMessage")
                    ApiResult.Error(response.code, errorMessage)
                }
            }
        } catch (e: Exception) {
            Log.e("ApiServiceCommon", "응답 처리 중 예외 발생: ${e.message}", e)
            ApiResult.Error(message = "응답 처리 오류: ${e.message}")
        }
    }
}
