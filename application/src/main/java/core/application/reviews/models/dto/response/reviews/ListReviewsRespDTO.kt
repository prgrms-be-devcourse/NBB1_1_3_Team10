package core.application.reviews.models.dto.response.reviews;

import core.application.reviews.models.entities.*;
import io.swagger.v3.oas.annotations.media.*;
import java.time.*;
import lombok.*;

@Data
@Schema(description = "조회된 리뷰 내용")
public class ListReviewsRespDTO {

    @Schema(description = "리뷰 ID")
    private Long reviewId;

    @Schema(description = "리뷰가 달린 영화 ID")
    private String movieId;

    @Schema(description = "리뷰 제목")
    private String title;

    @Schema(description = "리뷰 좋아요 수")
    private int likes;

    @Schema(description = "리뷰 생성 시각")
    private Instant createdAt;

    @Schema(description = "리뷰 수정 시각")
    private Instant updatedAt;

    /**
     * {@code Entity} 에서 {@code DTO} 로 변환
     */
    public static ListReviewsRespDTO of(ReviewEntity review) {
        ListReviewsRespDTO dto = new ListReviewsRespDTO();
        dto.reviewId = review.getReviewId();
        dto.movieId = review.getMovieId();
        dto.title = review.getTitle();
        dto.likes = review.getLike();
        dto.createdAt = review.getCreatedAt();
        dto.updatedAt = review.getUpdatedAt();

        return dto;
    }
}
