package core.application.reviews.models.dto.response.reviews

import core.application.reviews.models.entities.ReviewEntity
import io.swagger.v3.oas.annotations.media.Schema
import lombok.Data
import java.time.Instant

@Schema(description = "조회된 리뷰 내용")
data class ListReviewsRespDTO(
    @Schema(description = "리뷰 ID")
    var reviewId: Long,

    @Schema(description = "리뷰가 달린 영화 ID")
    var movieId: String? = null,

    @Schema(description = "리뷰 제목")
    var title: String,

    @Schema(description = "리뷰 좋아요 수")
    var likes:Int? = 0,

    @Schema(description = "리뷰 생성 시각")
    var createdAt: Instant?,

    @Schema(description = "리뷰 수정 시각")
    var updatedAt: Instant?
) {
    companion object {
        /**
         * `Entity` 에서 `DTO` 로 변환
         */
        @JvmStatic
        fun of(review: ReviewEntity): ListReviewsRespDTO {
            return ListReviewsRespDTO(
                reviewId = review.reviewId,
                movieId = review.movieId,
                title = review.title,
                likes = review.like,
                createdAt = review.createdAt,
                updatedAt = review.updatedAt
            )
        }
    }
}
