package core.application.reviews.repositories.jpa

import core.application.reviews.exceptions.NoReviewCommentFoundException
import core.application.reviews.models.entities.ReviewCommentEntity
import core.application.reviews.repositories.ReviewCommentRepository
import core.application.reviews.repositories.jpa.repositories.JpaReviewCommentRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@Profile("jpa")
class ReviewCommentRepositoryJpaImpl (
    private val jpaRepo: JpaReviewCommentRepository
) : ReviewCommentRepository {

    /**
     * {@inheritDoc}
     */
    override fun saveNewReviewComment(reviewComment: ReviewCommentEntity): ReviewCommentEntity? {
        return jpaRepo.save(reviewComment)
    }

    /**
     * {@inheritDoc}
     */
    override fun saveNewParentReviewComment(
        reviewId: Long,
        userId: UUID,
        reviewComment: ReviewCommentEntity
    ): ReviewCommentEntity? {
        val data: ReviewCommentEntity = ReviewCommentEntity(
            reviewId = reviewId,
            userId = userId,
            content = reviewComment.content,
            commentRef = reviewComment.commentRef,
            isUpdated = false
        )
        return jpaRepo.save(data)
    }

    /**
     * {@inheritDoc}
     */
    override fun saveNewChildReviewComment(
        groupId: Long,
        userId: UUID,
        reviewComment: ReviewCommentEntity
    ): ReviewCommentEntity? {
        val parent = jpaRepo.findById(groupId).orElseThrow {
            NoReviewCommentFoundException(
                groupId
            )
        }

        val data: ReviewCommentEntity = ReviewCommentEntity(
            reviewId = parent?.reviewId,
            userId = userId,
            content = reviewComment.content,
            groupId = groupId,
            commentRef = reviewComment.commentRef,
            isUpdated = false
        )

        return jpaRepo.save(data)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByReviewCommentId(reviewCommentId: Long): Optional<ReviewCommentEntity?> {
        return jpaRepo.findById(reviewCommentId)
    }

    /**
     * {@inheritDoc}
     */
    override fun findParentCommentByReviewId(
        reviewId: Long,
        offset: Int, num: Int
    ): List<ReviewCommentEntity?>? {
        return jpaRepo.findParentCommentByReviewId(reviewId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findParentCommentByReviewIdOnDateDescend(
        reviewId: Long,
        offset: Int, num: Int
    ): List<ReviewCommentEntity> {
        return jpaRepo.findParentCommentByReviewId(reviewId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findParentCommentByReviewIdOnLikeDescend(
        reviewId: Long,
        offset: Int, num: Int
    ): List<ReviewCommentEntity> {
        return jpaRepo.findParentCommentByReviewIdOnLikeDescend(reviewId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun countParentCommentByReviewId(reviewId: Long): Long {
        return jpaRepo.countParentCommentByReviewId(reviewId)
    }

    /**
     * {@inheritDoc}
     */
    override fun findChildCommentsByGroupId(groupId: Long, offset: Int, num: Int): List<ReviewCommentEntity> {
        return jpaRepo.findChildCommentsByGroupId(groupId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun countChildCommentByGroupId(groupId: Long): Long {
        return jpaRepo.countChildCommentByGroupId(groupId)
    }

    /**
     * {@inheritDoc}
     */
    override fun selectAllParentComments(): List<ReviewCommentEntity?>? {
        return jpaRepo.selectAllParentComments()
    }

    /**
     * {@inheritDoc}
     */
    override fun selectAll(): List<ReviewCommentEntity?>? {
        return jpaRepo.findAll()
    }

    /**
     * {@inheritDoc}
     */
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
        val find = jpaRepo.findById(reviewCommentId)
        if (find.isEmpty) {
            throw NoReviewCommentFoundException(reviewCommentId)
        }
        val origin = find.get()

        origin.changeContent(replacement.content)
        origin.changeCommentRef(replacement.commentRef)
        origin.isUpdated = update

        jpaRepo.save(origin)
        return jpaRepo.findById(origin.reviewCommentId)
    }

    /**
     * {@inheritDoc}
     */
    override fun updateReviewCommentLikes(reviewCommentId: Long, likes: Int): Optional<ReviewCommentEntity?> {
        val find = jpaRepo.findById(reviewCommentId)
        if (find.isEmpty) {
            throw NoReviewCommentFoundException(reviewCommentId)
        }
        val origin = find.get()

        origin.changeLikes(likes)

        jpaRepo.save(origin)
        return jpaRepo.findById(origin.reviewCommentId)
    }
}
