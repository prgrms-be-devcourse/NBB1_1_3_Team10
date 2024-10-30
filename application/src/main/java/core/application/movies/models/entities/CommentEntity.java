package core.application.movies.models.entities;

import core.application.movies.models.dto.request.CommentWriteReqDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Entity
@Table(name = "comment_table")
public class CommentEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;
	private String content;
	@Column(name = "`like`")
	private int like;
	private int dislike;
	private int rating;
	private String movieId;     // 영화 API 에 따라 달라질 수 있음.
	private UUID userId;
	@CreationTimestamp
	private Instant createdAt;

	public static CommentEntity of(CommentWriteReqDTO comment, String movieId, UUID userId) {
		return CommentEntity.builder()
			.content(comment.getContent())
			.like(0)
			.dislike(0)
			.rating(comment.getRating())
			.movieId(movieId)
			.userId(userId)
			.build();
	}

	public void isLiked() {
		this.like++;
	}

	public void cancelLike() {
		this.like--;
	}

	public void isDisliked() {
		this.dislike++;
	}

	public void cancelDislike() {
		this.dislike--;
	}
}
