package core.application.movies.models.entities

import jakarta.persistence.*
import lombok.*
import java.util.*

@Entity
@Table(name = "comment_dislike_table")
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
data class CommentDislike(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val commentDislikeId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    val comment: CommentEntity? = null,

    val userId: UUID? = null
) {
    companion object {
        fun of(comment: CommentEntity?, userId: UUID?): CommentDislike {
            return CommentDislike(
                commentDislikeId = null, // 주의: 이 값은 null로 설정되어 있으며, 생성 시 자동 생성됨
                comment = comment,
                userId = userId
            )
        }
    }
}

