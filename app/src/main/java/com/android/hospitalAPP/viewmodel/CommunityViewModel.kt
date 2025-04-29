package com.android.hospitalAPP.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.json.JSONArray
import org.json.JSONObject
import com.android.hospitalAPP.data.ApiResult
import com.android.hospitalAPP.data.ApiServiceCommon
import com.android.hospitalAPP.data.ApiConstants.NOTICES_URL
import com.android.hospitalAPP.data.PostRepository
import com.android.hospitalAPP.data.PostRepository.fetchPosts

class CommunityViewModel : ViewModel() {

    val posts: StateFlow<List<Post>> = PostRepository.posts

    private val _notices = MutableStateFlow<List<Notice>>(emptyList())
    val notices: StateFlow<List<Notice>> = _notices

    private val noticeService = NoticeService()

    init {
        fetchNotices()
        fetchPosts()
    }

    private fun fetchNotices() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("CommunityViewModel", "공지사항 데이터 요청 시작")
                when (val result = noticeService.getNotices()) {
                    is ApiResult.Success -> {
                        val jsonArray = result.data
                        Log.d("CommunityViewModel", "공지사항 데이터 수신: ${jsonArray.length()}개")

                        if (jsonArray.length() > 0) {
                            Log.d("CommunityViewModel", "첫 번째 항목: ${jsonArray.getJSONObject(0)}")
                        }

                        val noticeList = mutableListOf<Notice>()
                        for (i in 0 until jsonArray.length()) {
                            try {
                                val item = jsonArray.getJSONObject(i)
                                val notice = Notice(
                                    id = item.getInt("id"),
                                    title = item.getString("title"),
                                    comment = item.getString("comment"),
                                    image_urls = if (item.isNull("image_urls")) null else item.getString("image_urls"),
                                    created_at = item.getString("created_at"),
                                    views = item.optInt("views", 0),
                                    user_id = if (item.isNull("user_id")) null else item.getInt("user_id")
                                )
                                noticeList.add(notice)
                            } catch (e: Exception) {
                                Log.e("CommunityViewModel", "공지사항 항목 파싱 오류: ${e.message}")
                                continue
                            }
                        }

                        Log.d("CommunityViewModel", "공지사항 파싱 완료: ${noticeList.size}개")
                        _notices.value = noticeList
                    }

                    is ApiResult.Error -> {
                        Log.e("CommunityViewModel", "공지사항 요청 실패: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("CommunityViewModel", "공지사항 처리 중 예외 발생: ${e.message}")
            }
        }
    }

    fun addPost(title: String, content: String, writer: String) {
        PostRepository.addPost(title, content, writer)
    }

    data class Post(
        val id: Int,
        val title: String,
        val content: String,
        val writer: String,
        val timeAgo: String,
        val likes: Int,
        val comments: Int
    )

    data class Notice(
        val id: Int,
        val title: String,
        val comment: String,
        val image_urls: String?,
        val created_at: String,
        val views: Int,
        val user_id: Int?
    )

    inner class NoticeService {
        suspend fun getNotices(): ApiResult<JSONArray> = withContext(Dispatchers.IO) {
            try {
                val result = ApiServiceCommon.getRequest(NOTICES_URL)

                return@withContext when (result) {
                    is ApiResult.Success -> {
                        try {
                            val jsonObject = result.data as? JSONObject
                            val dataObject = jsonObject?.optJSONObject("data")
                            val dataArray = dataObject?.optJSONArray("items") ?: JSONArray()
                            ApiResult.Success(dataArray)
                        } catch (e: Exception) {
                            Log.e("NoticeService", "JSON 파싱 오류: ${e.message}")
                            ApiResult.Error(message = "데이터 파싱 중 오류가 발생했습니다: ${e.message}")
                        }
                    }

                    is ApiResult.Error -> {
                        ApiResult.Error(message = result.message)
                    }
                }
            } catch (e: Exception) {
                Log.e("NoticeService", "요청 처리 중 예외 발생: ${e.message}")
                ApiResult.Error(message = "요청 처리 중 오류가 발생했습니다: ${e.message}")
            }
        }
    }
}
