// CommentService.kt
package com.android.hospitalAPP.data

import com.android.hospitalAPP.data.ApiServiceCommon
import com.android.hospitalAPP.data.model.Comment
import okhttp3.FormBody
import org.json.JSONObject



object CommentService {

    /** Q&A 댓글 불러오기 */
    suspend fun getComments(qnaId: Int): List<Comment> {
        val url = "${ApiConstants.POSTS_URL}/$qnaId/comments"
        return when (val result = ApiServiceCommon.getRequest(url)) {
            is ApiResult.Success -> parseComments(result.data, qnaId)
            else                 -> emptyList()
        }
    }



    /** Q&A 댓글 / 대댓글 등록 */
    suspend fun postComment(
        qnaId: Int,
        text: String,
        parentId: Int? = null
    ): Boolean {
        val url = "${ApiConstants.POSTS_URL}/$qnaId/comments"
        val formBody = FormBody.Builder()
            .add("comment", text)
            .apply { parentId?.let { add("parent_id", it.toString()) } }
            .build()

        return when (ApiServiceCommon.postForm(url, formBody)) {
            is ApiResult.Success<*> -> true
            else                   -> false
        }
    }

    /** 서버가 내려주는 JSON을 Comment 모델 리스트로 변환 */
    private fun parseComments(json: JSONObject, qnaId: Int): List<Comment> {
        val flat = mutableListOf<Comment>()
        val arr = json.optJSONArray("comments") ?: return emptyList()
        for (i in 0 until arr.length()) {
            arr.optJSONObject(i)?.let { o ->
                flat += Comment(
                    id        = o.optInt("id"),
                    qnaId     = qnaId,
                    userId    = o.optInt("user_id"),//이거없엉
                    username  = o.optString("username"),
                    comment   = o.optString("comment"),
                    createdAt = o.optString("created_at"),
                    parentId  = o.optInt("parent_id").takeIf { it != 0 }
                    // replies 프로퍼티는 나중에 채워줄 거니까 디폴트(빈 MutableList)로 놔둡니다.
                )
            }
        }
        val byParent = flat.groupBy { it.parentId }
        return byParent[null]?.map { parent ->
            parent.copy(
                replies = (byParent[parent.id] ?: emptyList())
                    .toMutableList()   // ← List → MutableList 변환!
            )
        } ?: emptyList()
    }
}
