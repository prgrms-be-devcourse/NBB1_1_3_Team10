package core.application.reviews.models.entities

import jakarta.persistence.*
import lombok.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

/**
 * `ReviewRepository` 와 관련된 엔티티
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
class ReviewEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val reviewId: Long = 0,

    @Column(length = 50, nullable = false)
    var title: String,

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    var content: String? = null,

    @Column(length = 16, nullable = false)
    var userId: UUID? = null,

    @Column(length = 50, nullable = false)
    var movieId: String? = null,

    @Column(nullable = false, name = "`like`")
    var like:Int = 0,

    @CreationTimestamp
    @Column(nullable = false)
    var createdAt: Instant? = null,

    @Setter
    @CreationTimestamp
    var updatedAt: Instant? = null
) {

    fun updated() {
        this.updatedAt = Instant.now()
    }
    fun changeTitle(title: String) {
        this.title = title
    }

    fun changeContent(content: String?) {
        this.content = content
    }

    fun changeLikes(givenLikes: Int) {
        this.like = givenLikes
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val that = o as ReviewEntity
        return like == that.like && reviewId == that.reviewId
                && title == that.title && content == that.content && userId == that.userId
                && movieId == that.movieId && createdAt == that.createdAt && updatedAt == that.updatedAt
    }

    override fun hashCode(): Int {
        var result = Objects.hashCode(reviewId)
        result = 31 * result + Objects.hashCode(title)
        result = 31 * result + Objects.hashCode(content)
        result = 31 * result + Objects.hashCode(userId)
        result = 31 * result + Objects.hashCode(movieId)
        result = 31 * result + like
        result = 31 * result + Objects.hashCode(createdAt)
        result = 31 * result + Objects.hashCode(updatedAt)
        return result
    }

    companion object {
        fun copyOf(entity: ReviewEntity): ReviewEntity {
            return ReviewEntity (
                reviewId = entity.reviewId,
                title = entity.title,
                content = entity.content,
                userId = entity.userId,
                movieId = entity.movieId,
                like = entity.like,
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt
            )
        }
    }
}
