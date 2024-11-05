package core.application.reviews.models.entities

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*
import lombok.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.*

/**
 * `ReviewCommentRepository` 와 관련된 엔티티
 *
 * @see core.application.reviews.repositories.ReviewCommentRepository
 */
@Entity
@Table(name = "review_comment_table")
class ReviewCommentEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "댓글 고유 ID", example = "10015")
    val reviewCommentId: Long = 0,

    @Schema(description = "댓글이 달린 포스팅 고유 ID", example = "20")
    @Column(nullable = false)
    var reviewId: Long? = null,

    @Schema(description = "사용자 고유 ID")
    @Column(length = 16, nullable = false)
    var userId: UUID? = null,

    @Schema(description = "댓글 내용", example = "댓글 내용")
    @Column(length = 100, nullable = false)
    var content: String,

    @Schema(description = "부모 댓글의 고유 ID", nullable = true)
    val groupId: Long? = null,

    @Schema(description = "멘션된 댓글의 고유 ID", example = "10010", nullable = true)
    var commentRef: Long? = 0,

    @Schema(description = "댓글의 좋아요 수", example = "10")
    @Column(nullable = false, name = "`like`")
    var like:Int = 0,

    @Schema(description = "댓글의 생성 날자")
    @CreationTimestamp
    @Column(nullable = false)
    var createdAt: Instant? = null,

    @Schema(description = "댓글 수정 여부", example = "false")
    @Column(nullable = false)
    var isUpdated:Boolean = false
) {
    fun changeContent(content: String) {
        this.content = content
    }

    fun changeLikes(givenLikes: Int) {
        this.like = givenLikes
    }

    fun changeCommentRef(ref: Long?) {
        this.commentRef = ref
    }

    /**
     * 어느 댓글을 멘션하는 기능
     *
     *
     * `(자기자신) -> (target)`, 현재 엔티티가 `target` 을 멘션함.
     *
     * @param target 멘션 당할 `ReviewCommentEntity`
     * @return `this` 멘션 정보가 들어간 댓글 정보
     */
    fun mentionReviewComment(target: ReviewCommentEntity): ReviewCommentEntity {
        this.commentRef = target.reviewCommentId
        return this
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }

        val that = o as ReviewCommentEntity
        return like == that.like && isUpdated == that.isUpdated && reviewCommentId == that.reviewCommentId && reviewId == that.reviewId
                && userId == that.userId && content == that.content && groupId == that.groupId
                && commentRef == that.commentRef && createdAt == that.createdAt
    }

    override fun hashCode(): Int {
        var result = Objects.hashCode(reviewCommentId)
        result = 31 * result + Objects.hashCode(reviewId)
        result = 31 * result + Objects.hashCode(userId)
        result = 31 * result + Objects.hashCode(content)
        result = 31 * result + Objects.hashCode(groupId)
        result = 31 * result + Objects.hashCode(commentRef)
        result = 31 * result + like
        result = 31 * result + Objects.hashCode(createdAt)
        result = 31 * result + java.lang.Boolean.hashCode(isUpdated)
        return result
    }
}
