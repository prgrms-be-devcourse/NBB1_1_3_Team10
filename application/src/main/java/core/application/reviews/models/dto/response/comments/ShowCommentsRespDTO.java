package core.application.reviews.models.dto.response.comments;

import core.application.reviews.models.entities.*;
import io.swagger.v3.oas.annotations.media.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Data
@Schema(description = "조회된 댓글 내용")
public class ShowCommentsRespDTO {

    @Schema(description = "댓글 ID")
    private Long reviewCommentId;

    @Schema(description = "댓글 달린 포스팅 ID")
    private Long reviewId;

    @Schema(description = "댓글 작성자 ID")
    private UUID userId;

    @Schema(description = "부모 댓글 ID")
    private Long groupId;

    @Schema(description = "멘션 댓글 ID")
    private Long commentRef;

    @Schema(description = "댓글 내용")
    private String content;

    @Schema(description = "댓글 좋아요 수")
    private int likes;

    @Schema(description = "댓글 생성 시각")
    private Instant createdAt;

    @Schema(description = "댓글 수정 여부")
    private boolean isUpdated;

    /**
     * {@code Entity} 에서 {@code DTO} 로 변환
     */
    public static ShowCommentsRespDTO of(ReviewCommentEntity entity) {
        ShowCommentsRespDTO dto = new ShowCommentsRespDTO();
        dto.reviewCommentId = entity.getReviewCommentId();
        dto.reviewId = entity.getReviewId();
        dto.userId = entity.getUserId();
        dto.groupId = entity.getGroupId();
        dto.commentRef = entity.getCommentRef();
        dto.content = entity.getContent();
        dto.likes = entity.getLike();
        dto.createdAt = entity.getCreatedAt();
        dto.isUpdated = entity.isUpdated();

        return dto;
    }
}
