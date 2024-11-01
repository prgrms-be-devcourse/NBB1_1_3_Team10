package core.application.movies.models.entities

import core.application.movies.models.dto.request.CommentWriteReqDTO
import jakarta.persistence.*
import lombok.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

@Entity
@Table(name = "comment_table")
data class CommentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val commentId: Long? = null,

    val content: String? = null,

    @Column(name = "`like`")
    var like: Int = 0, // 기본값 0으로 설정

    var dislike: Int = 0, // 기본값 0으로 설정

    var rating: Int = 0, // 기본값 0으로 설정

    val movieId: String? = null, // 영화 API에 따라 달라질 수 있음

    val userId: UUID? = null,

    @CreationTimestamp
    val createdAt: Instant? = null
) {
    companion object {
        @JvmStatic
        fun of(comment: CommentWriteReqDTO, movieId: String?, userId: UUID?): CommentEntity {
            return CommentEntity(
                content = comment.content,
                like = 0,
                dislike = 0,
                rating = comment.rating,
                movieId = movieId,
                userId = userId
            )
        }
    }

    fun isLiked() {
        like++
    }

    fun cancelLike() {
        like--
    }

    fun isDisliked() {
        dislike++
    }

    fun cancelDislike() {
        dislike--
    }
}
