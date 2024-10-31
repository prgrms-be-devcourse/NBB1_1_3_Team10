package core.application.reviews.repositories.mybatis

import core.application.reviews.models.entities.ReviewCommentEntity
import core.application.reviews.repositories.ReviewCommentRepository
import core.application.reviews.repositories.mybatis.mappers.ReviewCommentMapper
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Slf4j
@Repository
@Profile("mybatis")
@RequiredArgsConstructor
class MyBatisReviewCommentRepository(
    private val mapper: ReviewCommentMapper
) : ReviewCommentRepository {

    /**
     * {@inheritDoc}
     */
    override fun saveNewReviewComment(reviewComment: ReviewCommentEntity): ReviewCommentEntity {
        val result = mapper.insertReviewComment(reviewComment)
        return reviewComment
    }

    /**
     * {@inheritDoc}
     */
    override fun saveNewParentReviewComment(
        reviewId: Long, userId: UUID,
        reviewComment: ReviewCommentEntity
    ): ReviewCommentEntity {
        // 보무 댓글은 groupId null

        val data: ReviewCommentEntity = ReviewCommentEntity(
            reviewId = reviewId,
            userId = userId,
            content = reviewComment.content,
            commentRef = reviewComment.commentRef,
            createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS)
        )

        return this.saveNewReviewComment(data)
    }

    /**
     * {@inheritDoc}
     */
    override fun saveNewChildReviewComment(
        groupId: Long, userId: UUID,
        reviewComment: ReviewCommentEntity
    ): ReviewCommentEntity {
        val data: ReviewCommentEntity = ReviewCommentEntity(
            reviewId = reviewComment.reviewId,
            userId = userId,
            content = reviewComment.content,
            groupId = groupId,
            commentRef = reviewComment.commentRef,
            createdAt = Instant.now().truncatedTo(ChronoUnit.SECONDS)
        )

        return this.saveNewReviewComment(data)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByReviewCommentId(reviewCommentId: Long): Optional<ReviewCommentEntity?> {
        return mapper.findByReviewCommentId(reviewCommentId)
    }

    /**
     * {@inheritDoc}
     */
    override fun findParentCommentByReviewId(
        reviewId: Long, offset: Int,
        num: Int
    ): List<ReviewCommentEntity> {
        return mapper.findParentCommentByReviewId(reviewId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findParentCommentByReviewIdOnDateDescend(
        reviewId: Long,
        offset: Int, num: Int
    ): List<ReviewCommentEntity> {
        return mapper.findParentCommentByReviewIdOnDateDescend(reviewId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findParentCommentByReviewIdOnLikeDescend(
        reviewId: Long,
        offset: Int, num: Int
    ): List<ReviewCommentEntity> {
        return mapper.findParentCommentByReviewIdOnLikeDescend(reviewId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun countParentCommentByReviewId(reviewId: Long): Long {
        return mapper.countParentCommentByReviewId(reviewId)
    }

    /**
     * {@inheritDoc}
     */
    override fun findChildCommentsByGroupId(groupId: Long, offset: Int, num: Int): List<ReviewCommentEntity> {
        return mapper.findChildCommentsByGroupId(groupId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun countChildCommentByGroupId(groupId: Long): Long {
        return mapper.countChildCommentByGroupId(groupId)
    }

    /**
     * {@inheritDoc}
     */
    override fun selectAllParentComments(): List<ReviewCommentEntity> {
        return mapper.selectAllParentComments()
    }

    /**
     * {@inheritDoc}
     */
    override fun selectAll(): List<ReviewCommentEntity> {
        return mapper.selectAll()
    }

    /**
     * {@inheritDoc}
     *
     */
    @Deprecated("use instead {@link #editReviewCommentInfo(Long, ReviewCommentEntity, boolean)}")
    override fun editReviewCommentInfo(
        reviewCommentId: Long,
        replacement: ReviewCommentEntity
    ): Optional<ReviewCommentEntity?> {
        return this.editReviewCommentInfo(reviewCommentId, replacement, true)
    }

    /**
     * {@inheritDoc}
     */
    override fun editReviewCommentInfo(
        reviewCommentId: Long,
        replacement: ReviewCommentEntity, update: Boolean
    ): Optional<ReviewCommentEntity?> {
        val result = mapper.updateReviewCommentEntity(reviewCommentId, replacement, update)
        return findByReviewCommentId(reviewCommentId)
    }

    /**
     * {@inheritDoc}
     */
    override fun updateReviewCommentLikes(reviewCommentId: Long, likes: Int): Optional<ReviewCommentEntity?> {
        val result = mapper.updateCommentLikes(reviewCommentId, likes)
        return findByReviewCommentId(reviewCommentId)
    }
}
