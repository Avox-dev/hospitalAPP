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
import com.example.compose.data.User

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
        val writer = postJson.optString("writer","writer")
        val createdAt = postJson.optString("created_at", "")
        val likes = postJson.optInt("likes", 0)
        val comments = postJson.optInt("comments", 0)

        // 시간 변환
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


    // 게시글 추가 메서드 - API 연동
    fun addPost(title: String, content: String, writer: String) {
        coroutineScope.launch {
            try {
                val result = createPostApi(title, content, writer)

                if (result is ApiResult.Success) {
                    val jsonResponse = result.data
                    val status = jsonResponse.optString("status")

                    if (status == "success") {
                        // 새 게시글 추가 후 전체 목록 다시 로드
                        fetchPosts()
                    } else {
                        // API 요청은 성공했지만 서버에서 오류 반환
                        println("게시글 추가 실패: ${jsonResponse.optString("message", "알 수 없는 오류")}")
                        addPostLocally(title, content, writer)
                    }
                } else if (result is ApiResult.Error) {
                    // API 요청 자체가 실패
                    println("게시글 추가 요청 실패: ${result.message}")
                    addPostLocally(title, content, writer)
                }
            } catch (e: Exception) {
                println("게시글 추가 중 예외 발생: ${e.message}")
                addPostLocally(title, content, writer)
            }
        }
    }

    // 게시글 생성 API 요청 - ApiServiceCommon 활용
    // PostRepository.kt의 createPostApi 메서드 수정
    private suspend fun createPostApi(title: String, content: String, writer: String): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        // UserRepository에서 현재 로그인한 사용자 정보 가져오기
        val userRepository = UserRepository.getInstance()
        val currentUser = userRepository.currentUser.value

        val username = currentUser?.userName ?: "익명" // 로그인되지 않은 경우 "익명"으로 표시

        val jsonBody = JSONObject().apply {
            put("title", title)
            put("comment", content)  // API는 comment 필드 사용
            put("username", writer) // 실제 현재 로그인한 사용자 이름 사용
        }

        return@withContext ApiServiceCommon.postRequest(ApiConstants.POSTS_URL, jsonBody)
    }

    // API 호출 실패 시 로컬에 게시글 추가 (임시 방편)
    // addPostLocally 메서드 수정
    private fun addPostLocally(title: String, content: String, writer: String) {
        val userRepository = UserRepository.getInstance()
        val currentUser = userRepository.currentUser.value

        val username = currentUser?.userName ?: "익명" // 로그인되지 않은 경우 "익명"으로 표시

        val newId = System.currentTimeMillis().toString()
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
