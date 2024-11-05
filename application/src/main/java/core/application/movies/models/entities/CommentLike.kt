package core.application.movies.models.entities

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "comment_like_table")
data class CommentLike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val commentLikeId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    val comment: CommentEntity? = null,

    val userId: UUID? = null
) {
    companion object {
        fun of(comment: CommentEntity?, userId: UUID?): CommentLike {
            return CommentLike(
                comment = comment,
                userId = userId
            )
        }
    }
}

