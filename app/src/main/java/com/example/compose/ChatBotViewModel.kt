// ChatBotViewModel.kt
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class ChatBotViewModel : ViewModel() {
    // 채팅 메시지 목록
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // OkHttp 클라이언트 설정
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // API 서버 URL (실제 URL로 변경 필요)
    private val apiUrl = "https://your-api-server.com/api/chat"

    // 사용자 메시지 전송
    fun sendMessage(message: String) {
        viewModelScope.launch {
            // 사용자 메시지 추가
            addMessage(message, true)

            // API 호출 시작 (로딩 상태 변경)
            _isLoading.value = true

            try {
                // API 호출
                val response = callChatApi(message)

                // 응답 추가
                addMessage(response, false)
            } catch (e: Exception) {
                // 오류 발생 시
                addMessage("죄송합니다. 응답을 받아오는 중 오류가 발생했습니다: ${e.message}", false)
            } finally {
                // 로딩 상태 해제
                _isLoading.value = false
            }
        }
    }

    // 메시지 추가 함수
    private fun addMessage(text: String, isFromUser: Boolean) {
        val newMessage = ChatMessage(text, isFromUser)
        _messages.value = _messages.value + newMessage
    }

    // API 호출 함수
    private suspend fun callChatApi(message: String): String = withContext(Dispatchers.IO) {
        try {
            // 실제 API 연동 시 사용할 코드
            val jsonBody = JSONObject().apply {
                put("message", message)
                // 필요한 경우 추가 파라미터 설정
                // put("user_id", "사용자ID")
            }

            val requestBody = jsonBody.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

            val request = Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build()

            // API 요청 실행
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("API 호출 실패: ${response.code}")
                }

                // 응답 처리
                val responseBody = response.body?.string() ?: throw Exception("응답이 비어있습니다")
                val jsonResponse = JSONObject(responseBody)

                // 응답에서 메시지 추출 (API 응답 형식에 맞게 수정 필요)
                return@withContext jsonResponse.optString("response", "응답을 받지 못했습니다")
            }

            // 아래는 API 연동 전 테스트용 코드입니다.
            // 실제 구현 시 위 코드를 사용하고 아래 코드는 제거하세요.
            /*
            // 테스트용 딜레이
            delay(1000)

            // 메시지에 따른 응답 (테스트용)
            return@withContext when {
                message.contains("안녕") -> "안녕하세요! 건강 관련 질문이 있으신가요?"
                message.contains("두통") -> "두통은 다양한 원인으로 발생할 수 있습니다. 지속적인 두통이라면 의사와 상담하는 것이 좋습니다."
                message.contains("감기") -> "감기 증상이 있으신가요? 충분한 휴식과 수분 섭취가 중요합니다. 증상이 심하다면, 병원에 방문하시는 것을 권장합니다."
                message.contains("열") || message.contains("발열") -> "발열은 체온이 37.5°C 이상일 때를 말합니다. 발열이 지속된다면 병원에 방문하시는 것이 좋습니다."
                else -> "죄송합니다, 질문을 이해하지 못했습니다. 좀 더 자세히 말씀해주시겠어요?"
            }
            */
        } catch (e: Exception) {
            // 테스트 중이라면 실제 API 호출 대신 이 코드를 사용하세요
            delay(1000)

            return@withContext when {
                message.contains("안녕") -> "안녕하세요! 건강 관련 질문이 있으신가요?"
                message.contains("두통") -> "두통은 다양한 원인으로 발생할 수 있습니다. 지속적인 두통이라면 의사와 상담하는 것이 좋습니다."
                message.contains("감기") -> "감기 증상이 있으신가요? 충분한 휴식과 수분 섭취가 중요합니다. 증상이 심하다면, 병원에 방문하시는 것을 권장합니다."
                message.contains("열") || message.contains("발열") -> "발열은 체온이 37.5°C 이상일 때를 말합니다. 발열이 지속된다면 병원에 방문하시는 것이 좋습니다."
                else -> "죄송합니다, 질문을 이해하지 못했습니다. 좀 더 자세히 말씀해주시겠어요?"
            }

            // 실제 구현 시에는 예외 처리를 위해 아래 코드 사용
            // throw e
        }
    }
}