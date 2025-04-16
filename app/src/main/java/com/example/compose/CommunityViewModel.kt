// CommunityViewModel.kt - 수정
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import com.example.compose.data.PostRepository
import kotlinx.coroutines.flow.StateFlow

class CommunityViewModel : ViewModel() {
    // PostRepository에서 데이터 가져오기
    val posts: StateFlow<List<Post>> = PostRepository.posts
    val notices: StateFlow<List<Notice>> = PostRepository.notices

    // 게시글 추가 함수 - 실제 작업은 Repository에 위임
    fun addPost(title: String, content: String, category: String) {
        PostRepository.addPost(title, content, category)
    }

    // 데이터 클래스들
    data class Post(
        val id: String,
        val title: String,
        val content: String,
        val author: String,
        val category: String,
        val timeAgo: String,
        val likes: Int,
        val comments: Int
    )

    data class Notice(
        val id: String,
        val title: String,
        val content: String,
        val date: String,
        val isImportant: Boolean
    )
}