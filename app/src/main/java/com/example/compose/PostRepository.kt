// PostRepository.kt - API 통신을 위해 수정됨
package com.example.compose.data

import com.example.compose.viewmodel.CommunityViewModel.Post
import com.example.compose.viewmodel.CommunityViewModel.Notice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log
import org.json.JSONArray

// ✅ 게시글 및 공지사항 관리 리포지토리 (API 연동)
object PostRepository {
    // 게시글 목록 상태 저장
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    // 공지사항 목록 상태 저장
    private val _notices = MutableStateFlow<List<Notice>>(emptyList())
    val notices: StateFlow<List<Notice>> = _notices

    // API 통신을 위한 CoroutineScope
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val postService = PostService()
    init {
        // 앱 시작 시 게시글 불러오기
        fetchPosts()
    }


    /**
     * ✅ 서버에서 게시글 목록 불러오기 (QnA 데이터)
     */
    fun fetchPosts() {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                Log.d("PostRepository", "QnA 데이터 요청 시작")

                when (val result = postService.getPosts()) {
                    is ApiResult.Success -> {
                        val jsonArray = result.data
                        Log.d("PostRepository", "QnA 데이터 수신: ${jsonArray.length()}개")

                        if (jsonArray.length() > 0) {
                            Log.d("PostRepository", "첫 번째 항목: ${jsonArray.getJSONObject(0)}")
                        }

                        val postList = mutableListOf<Post>()
                        for (i in 0 until jsonArray.length()) {
                            try {
                                val item = jsonArray.getJSONObject(i)
                                val post = Post(
                                    id = item.getInt("id"),
                                    title = item.optString("title", ""),
                                    content = item.optString("comment", ""), // comment 필드 사용
                                    writer = item.optString("writer", "writer"),
                                    timeAgo = calculateTimeAgo(item.optString("created_at", "")),
                                    likes = item.optInt("likes", 0),
                                    comments = item.optInt("comments", 0)
                                )
                                postList.add(post)
                            } catch (e: Exception) {
                                Log.e("PostRepository", "QnA 항목 파싱 오류: ${e.message}")
                                continue
                            }
                        }

                        Log.d("PostRepository", "QnA 파싱 완료: ${postList.size}개")
                        _posts.value = postList
                    }

                    is ApiResult.Error -> {
                        Log.e("PostRepository", "QnA 요청 실패: ${result.message}")
                    }
                }
            } catch (e: Exception) {
                Log.e("PostRepository", "QnA 처리 중 예외 발생: ${e.message}")
            }
        }
    }

    /**
     * ✅ 게시글 목록 API 요청 (QnA용)
     */
    private suspend fun getPostsFromApi(page: Int = 1, perPage: Int = 20): ApiResult<JSONArray> = withContext(Dispatchers.IO) {
        try {
            val url = "${ApiConstants.POSTS_URL}?page=$page&per_page=$perPage"
            val result = ApiServiceCommon.getRequest(url)

            return@withContext when (result) {
                is ApiResult.Success -> {
                    try {
                        val jsonObject = result.data as? JSONObject
                        val dataObject = jsonObject?.optJSONObject("data")
                        val dataArray = dataObject?.optJSONArray("items") ?: JSONArray()
                        ApiResult.Success(dataArray)
                    } catch (e: Exception) {
                        Log.e("PostService", "QnA JSON 파싱 오류: ${e.message}")
                        ApiResult.Error(message = "QnA 데이터 파싱 오류: ${e.message}")
                    }
                }

                is ApiResult.Error -> {
                    ApiResult.Error(message = result.message)
                }
            }
        } catch (e: Exception) {
            Log.e("PostService", "QnA 요청 처리 중 예외 발생: ${e.message}")
            ApiResult.Error(message = "요청 처리 중 오류가 발생했습니다: ${e.message}")
        }
    }

    /**
     * ✅ JSON 객체를 Post 객체로 변환
     */
    private fun parsePostFromJson(postJson: JSONObject): Post {
        val id = postJson.getInt("id")
        val title = postJson.optString("title", "")
        val content = postJson.optString("comment", "")  // API는 comment 필드 사용
        val writer = postJson.optString("writer","writer")
        val createdAt = postJson.optString("created_at", "")
        val likes = postJson.optInt("likes", 0)
        val comments = postJson.optInt("comments", 0)

        // 작성 시간 가공
        val timeAgo = calculateTimeAgo(createdAt)

        return Post(
            id = id,
            title = title,
            content = content,
            writer = writer,
            timeAgo = timeAgo,
            likes = likes,
            comments = comments
        )
    }

    /**
     * ✅ 작성 시각을 '방금 전', 'n시간 전' 형식으로 변환
     */
    fun calculateTimeAgo(createdAt: String): String {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val createdDate = formatter.parse(createdAt)
            if (createdDate != null) {
                val currentTime = Date().time
                val timeDiff = currentTime - createdDate.time
                val minutes = timeDiff / (1000 * 60)

                when {
                    minutes < 1 -> "방금 전"
                    minutes < 60 -> "${minutes}분 전"
                    minutes < 60 * 24 -> "${minutes / 60}시간 전"
                    else -> "${minutes / 60 / 24}일 전"
                }
            } else {
                "날짜 오류"
            }
        } catch (e: Exception) {
            Log.e("DateParseError", "날짜 파싱 실패: $createdAt", e)
            "날짜 오류"
        }
    }


    /**
     * ✅ 게시글 추가 (API 연동)
     */
    fun addPost(title: String, content: String, writer: String) {
        coroutineScope.launch {
            try {
                val result = createPostApi(title, content, writer)

                if (result is ApiResult.Success) {
                    val jsonResponse = result.data
                    val status = jsonResponse.optString("status")

                    if (status == "success") {
                        // 성공 시, 목록 다시 불러오기
                        fetchPosts()
                    } else {
                        println("게시글 추가 실패: ${jsonResponse.optString("message", "알 수 없는 오류")}")
                        addPostLocally(title, content, writer)
                    }
                } else if (result is ApiResult.Error) {
                    println("게시글 추가 요청 실패: ${result.message}")
                    addPostLocally(title, content, writer)
                }
            } catch (e: Exception) {
                println("게시글 추가 중 예외 발생: ${e.message}")
                addPostLocally(title, content, writer)
            }
        }
    }

    /**
     * ✅ 게시글 추가 API 요청 (POST)
     */
    private suspend fun createPostApi(title: String, content: String, writer: String): ApiResult<JSONObject> = withContext(Dispatchers.IO) {




        val jsonBody = JSONObject().apply {
            put("title", title)
            put("comment", content)
            put("username", writer)
        }

        return@withContext ApiServiceCommon.postRequest(ApiConstants.POSTS_URL, jsonBody)
    }

    /**
     * ✅ API 실패 시, 로컬에 임시로 게시글 추가
     */
    private fun addPostLocally(title: String, content: String, writer: String) {
        val userRepository = UserRepository.getInstance()
        val currentUser = userRepository.currentUser.value

        val username = currentUser?.userName ?: "익명" // 로그인되지 않은 경우 "익명"으로 표시

        val newId = System.currentTimeMillis().toInt()
        val newPost = Post(
            id = newId,
            title = title,
            content = content,// 실제 사용자 이름 사용
            writer = username,
            timeAgo = "방금 전",
            likes = 0,
            comments = 0
        )

        val currentPosts = _posts.value.toMutableList()
        currentPosts.add(0, newPost)
        _posts.value = currentPosts
    }

}


class PostService {
    suspend fun getPosts(): ApiResult<JSONArray> = withContext(Dispatchers.IO) {
        try {
            val url = ApiConstants.POSTS_URL
            val result = ApiServiceCommon.getRequest(url)

            return@withContext when (result) {
                is ApiResult.Success -> {
                    try {
                        val jsonObject = result.data as? JSONObject
                        val dataObject = jsonObject?.optJSONObject("data")
                        val dataArray = dataObject?.optJSONArray("items") ?: JSONArray()
                        ApiResult.Success(dataArray)
                    } catch (e: Exception) {
                        Log.e("PostService", "QnA JSON 파싱 오류: ${e.message}")
                        ApiResult.Error(message = "QnA 데이터 파싱 오류: ${e.message}")
                    }
                }
                is ApiResult.Error -> {
                    ApiResult.Error(message = result.message)
                }
            }
        } catch (e: Exception) {
            Log.e("PostService", "QnA 요청 처리 중 예외 발생: ${e.message}")
            ApiResult.Error(message = "요청 처리 중 오류가 발생했습니다: ${e.message}")
        }
    }
}
