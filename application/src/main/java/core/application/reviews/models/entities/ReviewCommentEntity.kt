package core.application.reviews.models.entities;

import io.swagger.v3.oas.annotations.media.*;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.hibernate.annotations.*;

/**
 * {@code ReviewCommentRepository} 와 관련된 엔티티
 *
 * @see core.application.reviews.repositories.ReviewCommentRepository
 */
@Entity
@Table(name = "review_comment_table")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ReviewCommentEntity {

    /**
     * 포스팅 리뷰 댓글 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "댓글 고유 ID", example = "10015")
    private Long reviewCommentId;

    /**
     * 댓글이 달린 포스팅 ID
     */
    @Schema(description = "댓글이 달린 포스팅 고유 ID", example = "20")
    @Column(nullable = false)
    private Long reviewId;

    @Schema(description = "사용자 고유 ID")
    @Column(length = 16, nullable = false)
    private UUID userId;

    @Schema(description = "댓글 내용", example = "댓글 내용")
    @Column(length = 100, nullable = false)
    private String content;

    /**
     * 부모 댓글의 ID, 자신이 부모 댓글이면 {@code Null}
     * <p>
     * {@link #reviewCommentId} 와 동일
     */
    @Schema(description = "부모 댓글의 고유 ID", nullable = true)
    private Long groupId;

    /**
     * 현 댓글이 멘션하는 댓글의 ID
     * <p>
     * {@link #reviewCommentId} 와 동일
     */
    @Schema(description = "멘션된 댓글의 고유 ID", example = "10010", nullable = true)
    private Long commentRef;

    @Schema(description = "댓글의 좋아요 수", example = "10")
    @Column(nullable = false, name = "`like`")
    private int like;

    @Setter
    @Schema(description = "댓글의 생성 날자")
    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;

    @Setter
    @Schema(description = "댓글 수정 여부", example = "false")
    @Column(nullable = false)
    private boolean isUpdated;

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeLikes(int givenLikes) {
        this.like = givenLikes;
    }

    public void changeCommentRef(Long ref) {
        this.commentRef = ref;
    }
    /**
     * 어느 댓글을 멘션하는 기능
     * <p>
     * {@code (자기자신) -> (target)}, 현재 엔티티가 {@code target} 을 멘션함.
     *
     * @param target 멘션 당할 {@code ReviewCommentEntity}
     * @return {@code this} 멘션 정보가 들어간 댓글 정보
     */
    public ReviewCommentEntity mentionReviewComment(ReviewCommentEntity target) {
        this.commentRef = target.getReviewCommentId();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReviewCommentEntity that = (ReviewCommentEntity) o;
        return like == that.like && isUpdated == that.isUpdated && Objects.equals(
            reviewCommentId, that.reviewCommentId) && Objects.equals(reviewId, that.reviewId)
            && Objects.equals(userId, that.userId) && Objects.equals(content,
            that.content) && Objects.equals(groupId, that.groupId)
            && Objects.equals(commentRef, that.commentRef) && Objects.equals(
            createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(reviewCommentId);
        result = 31 * result + Objects.hashCode(reviewId);
        result = 31 * result + Objects.hashCode(userId);
        result = 31 * result + Objects.hashCode(content);
        result = 31 * result + Objects.hashCode(groupId);
        result = 31 * result + Objects.hashCode(commentRef);
        result = 31 * result + like;
        result = 31 * result + Objects.hashCode(createdAt);
        result = 31 * result + Boolean.hashCode(isUpdated);
        return result;
    }
}
