package core.application.reviews.models.entities;

import jakarta.persistence.Table;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.hibernate.annotations.*;

/**
 * {@code  ReviewRepository} 와 관련된 엔티티
 *
 * @see core.application.reviews.repositories.ReviewRepository
 */
@Entity
@Table(name = "review_table")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(length = 50, nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @Column(length = 16, nullable = false)
    private UUID userId;

    @Column(length = 50, nullable = false)
    private String movieId;

    @Column(nullable = false, name = "`like`")
    private int like;

    @Setter
    @CreationTimestamp
    @Column(nullable = false)
    private Instant createdAt;

    @Setter
    @CreationTimestamp
    private Instant updatedAt;

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }

    public void changeLikes(int givenLikes) {
        this.like = givenLikes;
    }

    public static ReviewEntity copyOf(ReviewEntity entity) {
        return ReviewEntity.builder()
                .reviewId(entity.getReviewId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .userId(entity.getUserId())
                .movieId(entity.getMovieId())
                .like(entity.getLike())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ReviewEntity that = (ReviewEntity) o;
        return like == that.like && Objects.equals(reviewId, that.reviewId)
                && Objects.equals(title, that.title) && Objects.equals(content,
                that.content) && Objects.equals(userId, that.userId)
                && Objects.equals(movieId, that.movieId) && Objects.equals(
                createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(reviewId);
        result = 31 * result + Objects.hashCode(title);
        result = 31 * result + Objects.hashCode(content);
        result = 31 * result + Objects.hashCode(userId);
        result = 31 * result + Objects.hashCode(movieId);
        result = 31 * result + like;
        result = 31 * result + Objects.hashCode(createdAt);
        result = 31 * result + Objects.hashCode(updatedAt);
        return result;
    }
}
