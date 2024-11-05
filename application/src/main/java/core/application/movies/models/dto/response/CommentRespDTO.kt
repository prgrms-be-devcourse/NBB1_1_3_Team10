package core.application.movies.models.dto.response

import core.application.movies.models.entities.CommentEntity
import io.swagger.v3.oas.annotations.media.Schema
import lombok.Builder
import lombok.Data
import java.time.Instant

@Data
@Builder
@Schema(description = "한줄평 작성 응답 정보")
data class CommentRespDTO (
    @Schema(description = "한줄평 ID", example = "1")
    val commentId: Long? = null,

    @Schema(description = "한줄평 작성 내용", example = "정말 재밌는 영화네요.")
    val cmtContent: String? = null,

    @Schema(description = "좋아요 개수", example = "0")
    val like: Int = 0,

    @Schema(description = "싫어요 개수", example = "0")
    val dislike: Int = 0,

    @Schema(description = "평점", example = "9")
    val rating: Int = 0,

    @Schema(description = "한줄평 남긴 영화 ID", example = "A-12345")
    val movieId: String? = null,

    @Schema(description = "한줄평을 작성한 유저", example = "kim")
    val alias: String? = null,

    @Schema(description = "한줄평 작성 시간", example = "2024-10-05")
    val createdAt: Instant? = null,

    @Schema(description = "현재 사용자가 해당 한줄평 좋아요 여부", example = "false")
    val isLiked: Boolean = false,

    @Schema(description = "현재 사용자가 해당 한줄평 싫어요 여부", example = "false")
    val isDisliked: Boolean = false,
){
    companion object {
        @JvmStatic
        fun of(comment: CommentEntity, userAlias: String?): CommentRespDTO {
            return CommentRespDTO(
                commentId = comment.commentId,
                movieId = comment.movieId,
                cmtContent = comment.content,
                alias = userAlias,
                like = comment.like,
                dislike = comment.dislike,
                rating = comment.rating,
                createdAt = comment.createdAt,
                isLiked = false,
                isDisliked = false
            )
        }
    }
}
