package core.application.reviews.models.dto.response.comments

import core.application.reviews.models.entities.ReviewCommentEntity
import io.swagger.v3.oas.annotations.media.Schema
import lombok.Data
import java.time.Instant
import java.util.*

@Schema(description = "조회된 댓글 내용")
data class ShowCommentsRespDTO(
    @Schema(description = "댓글 ID")
    var reviewCommentId: Long,

    @Schema(description = "댓글 달린 포스팅 ID")
    var reviewId: Long?,

    @Schema(description = "댓글 작성자 ID")
    var userId: UUID?,

    @Schema(description = "부모 댓글 ID")
    var groupId: Long?,

    @Schema(description = "멘션 댓글 ID")
    var commentRef: Long?,

    @Schema(description = "댓글 내용")
    var content: String,

    @Schema(description = "댓글 좋아요 수")
    var likes:Int? = 0,

    @Schema(description = "댓글 생성 시각")
    var createdAt: Instant?,

    @Schema(description = "댓글 수정 여부")
    var isUpdated:Boolean? = false
) {
    companion object {
        /**
         * `Entity` 에서 `DTO` 로 변환
         */
        @JvmStatic
        fun of(entity: ReviewCommentEntity): ShowCommentsRespDTO {
            return ShowCommentsRespDTO(
                reviewCommentId = entity.reviewCommentId,
                reviewId = entity.reviewId,
                userId = entity.userId,
                groupId = entity.groupId,
                commentRef = entity.commentRef,
                content = entity.content,
                likes = entity.like,
                createdAt = entity.createdAt,
                isUpdated = entity.isUpdated
            )
        }
    }
}
