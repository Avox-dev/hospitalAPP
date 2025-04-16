// CommunityViewModel.kt
package com.example.compose.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CommunityViewModel : ViewModel() {

    // 게시글 목록을 위한 StateFlow
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    init {
        // 샘플 데이터 로드
        loadSamplePosts()
    }

    private fun loadSamplePosts() {
        val samplePosts = listOf(
            Post(
                id = "1",
                title = "아기가 자꾸 열이 나요, 어떻게 해야 할까요?",
                content = "6개월 된 아기인데 어제부터 열이 38도까지 올라갔어요. 해열제를 먹였는데도 잘 안 내려가네요. 병원에 가볼까요?",
                author = "워킹맘",
                category = "질문",
                timeAgo = "10분 전",
                likes = 5,
                comments = 3
            ),
            Post(
                id = "2",
                title = "임산부를 위한 영양제 추천해주세요",
                content = "임신 12주차인데 입덧이 심해서 영양제 먹으려고 합니다. 추천 부탁드려요!",
                author = "예비맘",
                category = "질문",
                timeAgo = "30분 전",
                likes = 8,
                comments = 12
            ),
            Post(
                id = "3",
                title = "저희 동네 소아과 추천해요",
                content = "강남역 근처에 있는 해피키즈 소아과 정말 좋아요. 의사선생님도 친절하시고 대기시간도 짧아요. 아이들 놀이공간도 깨끗하게 관리되고 있어서 추천합니다.",
                author = "아이맘",
                category = "정보공유",
                timeAgo = "1시간 전",
                likes = 15,
                comments = 4
            ),
            Post(
                id = "4",
                title = "아이 식욕부진 어떻게 해결하셨나요?",
                content = "4살 아들이 요즘 밥을 잘 안 먹어요. 간식만 찾고 밥은 몇 숟가락 먹기 힘든 상황입니다. 비슷한 경험 있으신 분들 조언 부탁드려요.",
                author = "고민맘",
                category = "질문",
                timeAgo = "3시간 전",
                likes = 7,
                comments = 9
            ),
            Post(
                id = "5",
                title = "올 여름 가족여행 계획",
                content = "아이들과 함께 갈만한 여름 여행지 고민 중이에요. 작년에는 제주도 다녀왔는데 올해는 어디가 좋을까요?",
                author = "여행좋아",
                category = "일상",
                timeAgo = "5시간 전",
                likes = 12,
                comments = 8
            ),
            Post(
                id = "6",
                title = "육아휴직 후 복직 고민",
                content = "곧 육아휴직이 끝나는데 복직할지 퇴사할지 고민이에요. 워킹맘으로 살아가시는 분들 조언 부탁드려요.",
                author = "맘고민",
                category = "자유",
                timeAgo = "6시간 전",
                likes = 20,
                comments = 15
            ),
            Post(
                id = "7",
                title = "신생아 목욕 팁 공유",
                content = "우리 아기 목욕 시키는 노하우를 공유합니다. 처음에는 너무 무서웠는데 이렇게 하니까 편해요.",
                author = "초보맘",
                category = "정보공유",
                timeAgo = "8시간 전",
                likes = 25,
                comments = 7
            ),
            Post(
                id = "8",
                title = "소아과 의사가 알려주는 감기 예방법",
                content = "안녕하세요, 소아과 의사입니다. 환절기 아이들 감기 예방하는 방법 몇 가지 알려드릴게요.",
                author = "닥터맘",
                category = "정보공유",
                timeAgo = "10시간 전",
                likes = 45,
                comments = 12
            ),
            Post(
                id = "9",
                title = "오늘 아이와 함께한 즐거운 시간",
                content = "주말에 아이와 함께 공원에 다녀왔어요. 날씨도 좋고 정말 행복한 시간이었습니다.",
                author = "행복맘",
                category = "일상",
                timeAgo = "12시간 전",
                likes = 18,
                comments = 5
            ),
            Post(
                id = "10",
                title = "육아 스트레스 푸는 방법",
                content = "육아하면서 스트레스 너무 많이 받는데 여러분은 어떻게 푸시나요? 저는 아이 재운 후 혼자 와인 한잔하는게 힐링이에요.",
                author = "와인맘",
                category = "자유",
                timeAgo = "1일 전",
                likes = 32,
                comments = 24
            )
        )

        _posts.value = samplePosts
    }

    // 게시글 데이터 클래스
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
}