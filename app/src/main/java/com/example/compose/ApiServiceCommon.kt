package com.example.compose.data

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int? = null, val message: String) : ApiResult<Nothing>()
}

object ApiServiceCommon {

    private val client = OkHttpClient.Builder()
        .connectTimeout(ApiConstants.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(ApiConstants.READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(ApiConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
        .build()

    suspend fun postRequest(url: String, jsonBody: JSONObject): ApiResult<JSONObject> {
        return try {
            Log.d("ApiServiceCommon", "POST 요청 URL: $url")
            Log.d("ApiServiceCommon", "POST 요청 Body: $jsonBody")

            val requestBody = jsonBody.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
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
