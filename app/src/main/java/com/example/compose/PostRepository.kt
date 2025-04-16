// PostRepository.kt - 새로 생성
package com.example.compose.data

import com.example.compose.viewmodel.CommunityViewModel.Post
import com.example.compose.viewmodel.CommunityViewModel.Notice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PostRepository {
    // 게시글 목록
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    // 공지사항 목록
    private val _notices = MutableStateFlow<List<Notice>>(emptyList())
    val notices: StateFlow<List<Notice>> = _notices

    init {
        // 샘플 데이터 로드
        loadSamplePosts()
        loadSampleNotices()
    }

    // 게시글 추가 메서드
    fun addPost(title: String, content: String, category: String) {
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

    // 샘플 데이터 로드 메서드
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
                content = "강남역 근처에 있는 해피키즈 소아과 정말 좋아요. 의사선생님도 친절하시고 대기시간도 짧아요.",
                author = "아이맘",
                category = "자유",
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
                category = "자유",
                timeAgo = "5시간 전",
                likes = 12,
                comments = 8
            )
        )

        _posts.value = samplePosts
    }

    private fun loadSampleNotices() {
        val sampleNotices = listOf(
            Notice(
                id = "1",
                title = "어플리케이션 업데이트 안내",
                content = "안녕하세요. 병원 애플리케이션이 새롭게 업데이트 되었습니다. 자세한 내용은 본문을 확인해주세요.",
                date = "2025.04.15",
                isImportant = true
            ),
            Notice(
                id = "2",
                title = "개인정보 처리방침 개정 안내",
                content = "안녕하세요. 당사의 개인정보 처리방침이 2025년 4월 1일부로 개정되었음을 알려드립니다.",
                date = "2025.04.01",
                isImportant = true
            ),
            Notice(
                id = "3",
                title = "2025년 봄철 예방접종 안내",
                content = "봄철을 맞아 어린이 예방접종 일정을 안내해드립니다. 병원 내 감염 예방을 위해 사전 예약 후 방문해주시기 바랍니다.",
                date = "2025.03.20",
                isImportant = false
            ),
            Notice(
                id = "4",
                title = "어린이 건강검진 무료 이벤트",
                content = "5월 가정의 달을 맞아 어린이 건강검진 무료 이벤트를 실시합니다. 자세한 내용은 본문을 참고해주세요.",
                date = "2025.03.15",
                isImportant = false
            ),
            Notice(
                id = "5",
                title = "코로나19 예방 수칙 안내",
                content = "최근 코로나19 변이 바이러스 관련 주의사항 및 예방 수칙을 안내해드립니다.",
                date = "2025.03.10",
                isImportant = false
            )
        )

        _notices.value = sampleNotices
    }
}