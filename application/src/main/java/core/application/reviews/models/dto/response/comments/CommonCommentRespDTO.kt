package core.application.reviews.models.dto.response.comments

import lombok.experimental.Accessors
import java.time.Instant
import java.util.*

@Accessors(chain = true)
data class CommonCommentRespDTO(
    val reviewCommentId: Long,
    val groupId: Long,
    val commentRef: Long,
    val userId: UUID,
    val content: String,
    val createdAt: Instant,
    val likeCount: Int? = 0,
    val isUpdated: Boolean? = false
)
