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

object PostRepository {
    // 게시글 목록
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    // 공지사항 목록
    private val _notices = MutableStateFlow<List<Notice>>(emptyList())
    val notices: StateFlow<List<Notice>> = _notices

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {

        fetchPosts()
    }

    // API에서 게시글 목록 가져오기
    fun fetchPosts() {
        coroutineScope.launch {
            try {
                val result = getPostsFromApi()

                if (result is ApiResult.Success) {
                    val jsonResponse = result.data
                    val status = jsonResponse.optString("status")

                    if (status == "success") {
                        val dataObject = jsonResponse.optJSONObject("data")
                        if (dataObject != null) {
                            val itemsArray = dataObject.optJSONArray("items")
                            if (itemsArray != null) {
                                val postsList = mutableListOf<Post>()

                                for (i in 0 until itemsArray.length()) {
                                    val postJson = itemsArray.getJSONObject(i)
                                    val post = parsePostFromJson(postJson)
                                    postsList.add(post)
                                }

                                _posts.value = postsList
                                return@launch
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("API 요청 실패: ${e.message}")
            }
        }
    }

    // API 요청 함수 (GET) - ApiServiceCommon 활용
    private suspend fun getPostsFromApi(page: Int = 1, perPage: Int = 20): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        val url = "${ApiConstants.POSTS_URL}?page=$page&per_page=$perPage"
        return@withContext ApiServiceCommon.getRequest(url)
    }

    // JSON에서 Post 객체로 파싱
    private fun parsePostFromJson(postJson: JSONObject): Post {
        val id = postJson.optString("id", "")
        val title = postJson.optString("title", "")
        val content = postJson.optString("comment", "")  // API는 comment 필드 사용
        val author = postJson.optString("username", "익명")
        val category = postJson.optString("category", "일반")
        val createdAt = postJson.optString("created_at", "")
        val likes = postJson.optInt("likes", 0)
        val comments = postJson.optInt("comments", 0)

        // 시간 변환
        val timeAgo = calculateTimeAgo(createdAt)

        return Post(
            id = id,
            title = title,
            content = content,
            author = author,
            category = category,
            timeAgo = timeAgo,
            likes = likes,
            comments = comments
        )
    }

    // 날짜 문자열을 "n시간 전" 형식으로 변환
    private fun calculateTimeAgo(dateStr: String): String {
        if (dateStr.isEmpty()) return "날짜 정보 없음"

        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val past = sdf.parse(dateStr)
            val now = Date()
            val seconds = (now.time - past.time) / 1000

            return when {
                seconds < 60 -> "방금 전"
                seconds < 3600 -> "${seconds / 60}분 전"
                seconds < 86400 -> "${seconds / 3600}시간 전"
                else -> "${seconds / 86400}일 전"
            }
        } catch (e: Exception) {
            return "날짜 오류"
        }
    }

    // 게시글 추가 메서드 - API 연동
    fun addPost(title: String, content: String, category: String) {
        coroutineScope.launch {
            try {
                val result = createPostApi(title, content, category)

                if (result is ApiResult.Success) {
                    val jsonResponse = result.data
                    val status = jsonResponse.optString("status")

                    if (status == "success") {
                        // 새 게시글 추가 후 전체 목록 다시 로드
                        fetchPosts()
                    } else {
                        // API 요청은 성공했지만 서버에서 오류 반환
                        println("게시글 추가 실패: ${jsonResponse.optString("message", "알 수 없는 오류")}")
                        addPostLocally(title, content, category)
                    }
                } else if (result is ApiResult.Error) {
                    // API 요청 자체가 실패
                    println("게시글 추가 요청 실패: ${result.message}")
                    addPostLocally(title, content, category)
                }
            } catch (e: Exception) {
                println("게시글 추가 중 예외 발생: ${e.message}")
                addPostLocally(title, content, category)
            }
        }
    }

    // 게시글 생성 API 요청 - ApiServiceCommon 활용
    private suspend fun createPostApi(title: String, content: String, category: String): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        val jsonBody = JSONObject().apply {
            put("title", title)
            put("comment", content)  // API는 comment 필드 사용
            put("category", category)
            put("username", "사용자")  // 필요에 따라 실제 사용자 이름으로 변경
        }

        return@withContext ApiServiceCommon.postRequest(ApiConstants.POSTS_URL, jsonBody)
    }

    // API 호출 실패 시 로컬에 게시글 추가 (임시 방편)
    private fun addPostLocally(title: String, content: String, category: String) {
        val newId = System.currentTimeMillis().toString()
        val newPost = Post(
            id = newId,
            title = title,
            content = content,
            author = "사용자",
            category = category,
            timeAgo = "방금 전",
            likes = 0,
            comments = 0
        )

        val currentPosts = _posts.value.toMutableList()
        currentPosts.add(0, newPost)
        _posts.value = currentPosts
    }

    // 게시글 상세 조회 API 요청 - ApiServiceCommon 활용
    private suspend fun getPostDetailFromApi(postId: String): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        val url = "${ApiConstants.POSTS_URL}/$postId"
        return@withContext ApiServiceCommon.getRequest(url)
    }

    // 게시글 수정 API 요청 - ApiServiceCommon 활용
    private suspend fun updatePostApi(postId: String, title: String, content: String, category: String): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        val jsonBody = JSONObject().apply {
            put("title", title)
            put("comment", content)
            put("category", category)
        }

        val url = "${ApiConstants.POSTS_URL}/$postId"
        // PUT 요청은 아직 ApiServiceCommon에 구현되어 있지 않아 POST로 대체
        return@withContext ApiServiceCommon.postRequest(url, jsonBody)
    }

    // 게시글 삭제 API 요청 - ApiServiceCommon 활용
    private suspend fun deletePostApi(postId: String): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        val url = "${ApiConstants.POSTS_URL}/$postId"
        // DELETE 요청은 아직 ApiServiceCommon에 구현되어 있지 않아 POST로 대체
        return@withContext ApiServiceCommon.postRequest(url, JSONObject())
    }


    }
