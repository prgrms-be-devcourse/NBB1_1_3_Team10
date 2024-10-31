package core.application.reviews.models.dto.response.comments

import core.application.reviews.models.entities.ReviewCommentEntity
import java.time.Instant
import java.util.*

data class CreateCommentRespDTO (
    val reviewCommentId: Long?,
    val groupId: Long?,
    val commentRef: Long?,
    val userId: UUID?,
    val content: String,
    val createdAt: Instant?,
    val likeCount:Int? = 0,
    val isUpdated:Boolean? = false
) {
    companion object {
        @JvmStatic
        fun toDTO(entity: ReviewCommentEntity): CreateCommentRespDTO {
            return CreateCommentRespDTO(
                reviewCommentId = entity.reviewId,
                groupId = entity.groupId,
                commentRef = entity.commentRef,
                userId = entity.userId,
                content = entity.content,
                likeCount = entity.like,
                createdAt = entity.createdAt,
                isUpdated = entity.isUpdated
            )
        }
    }
}
