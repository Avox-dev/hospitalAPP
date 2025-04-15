// ApiService.kt - Network related code
package com.example.compose.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class ApiService {
    private val client = OkHttpClient()

    suspend fun fetchTodoData(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://jsonplaceholder.typicode.com/todos/1")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext Result.failure(IOException("응답 본문 없음"))

            val json = JSONObject(body)
            val title = json.getString("title")
            Result.success("받은 제목: $title")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}