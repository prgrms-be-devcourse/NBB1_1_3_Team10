package core.application.reviews.services

import core.application.reviews.exceptions.NoReviewCommentFoundException
import core.application.reviews.exceptions.NoReviewFoundException
import core.application.reviews.models.entities.ReviewCommentEntity
import core.application.reviews.repositories.ReviewCommentRepository
import core.application.reviews.repositories.ReviewRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.function.Function
import java.util.function.Supplier

@Service
class ReviewCommentServiceImpl(
    private val reviewCommentRepo: ReviewCommentRepository,
    private val reviewRepo: ReviewRepository // 아직 bean 으로 등록 안되어 있어서 build 에러 날 수 있음.
) : ReviewCommentService {

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Throws(
        NoReviewCommentFoundException::class
    )
    override fun doesUserOwnsComment(userId: UUID, reviewCommentId: Long): Boolean {
        val reviewComment = doesExist(reviewCommentId,
            { reviewCommentId: Long -> reviewCommentRepo.findByReviewCommentId(reviewCommentId) },
            { NoReviewCommentFoundException(reviewCommentId) })
        val findByReviewCommentId = reviewCommentRepo.findByReviewCommentId(reviewCommentId)

        return reviewComment!!.userId == userId
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Throws(NoReviewFoundException::class)
    override fun getNumberOfParentComment(reviewId: Long): Long {
        // reviewId 에 해당하는 포스팅 없으면 throw

        doesExist(reviewId,
            { reviewId: Long -> reviewRepo.findByReviewId(reviewId) },
            { NoReviewFoundException(reviewId) })

        return reviewCommentRepo.countParentCommentByReviewId(reviewId)
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Throws(
        NoReviewCommentFoundException::class
    )
    override fun getNumberOfChildComment(groupId: Long): Long {
        // groupId 에 해당하는 부모 댓글 없으면 throw

        doesExist(groupId,
            { reviewCommentId: Long -> reviewCommentRepo.findByReviewCommentId(reviewCommentId) },
            { NoReviewCommentFoundException(groupId) })

        return reviewCommentRepo.countChildCommentByGroupId(groupId)
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Throws(NoReviewFoundException::class)
    override fun getParentReviewComments(
        reviewId: Long,
        order: ReviewCommentSortOrder, offset: Int, num: Int
    ): List<ReviewCommentEntity> {
        // reviewId 에 해당하는 포스팅 없으면 throw

        doesExist(reviewId,
            { reviewId: Long -> reviewRepo.findByReviewId(reviewId) },
            { NoReviewFoundException(reviewId) })

        return when (order) {
            ReviewCommentSortOrder.LATEST -> reviewCommentRepo.findParentCommentByReviewIdOnDateDescend(
                reviewId, offset,
                num
            )

            ReviewCommentSortOrder.LIKE -> reviewCommentRepo.findParentCommentByReviewIdOnLikeDescend(
                reviewId, offset,
                num
            )
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Throws(
        NoReviewFoundException::class,
        NoReviewCommentFoundException::class
    )
    override fun getChildReviewCommentsOnParent(
        reviewId: Long, groupId: Long, offset: Int, num: Int
    ): List<ReviewCommentEntity> {
        // 부모 댓글 없으면 throw

        val parentComment = doesExist(groupId,
            { reviewCommentId: Long -> reviewCommentRepo.findByReviewCommentId(reviewCommentId) },
            { NoReviewCommentFoundException(groupId) })

        // 자식 댓글 달려는 부모 댓글이 reviewId 의 댓글이 아니면 throw
        if (parentComment!!.reviewId != reviewId) {
            throw NoReviewCommentFoundException(
                ("Parent comment [" + groupId + "] does not belongs to given review ID ["
                        + reviewId + "]")
            )
        }

        return reviewCommentRepo.findChildCommentsByGroupId(groupId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Throws(NoReviewFoundException::class)
    override fun addNewParentReviewComment(
        reviewId: Long, userId: UUID,
        parentReviewComment: ReviewCommentEntity
    ): ReviewCommentEntity? {
        // 포스팅 없으면 throw

        doesExist(reviewId,
            { reviewId: Long -> reviewRepo.findByReviewId(reviewId) },
            { NoReviewFoundException(reviewId) })

        return reviewCommentRepo.saveNewParentReviewComment(reviewId, userId, parentReviewComment)
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Throws(
        NoReviewFoundException::class,
        NoReviewCommentFoundException::class
    )
    override fun addNewChildReviewComment(
        reviewId: Long, groupId: Long, userId: UUID,
        childReviewComment: ReviewCommentEntity
    ): ReviewCommentEntity? {
        // 포스팅, 부모 댓글 없으면 throw

        doesExist(reviewId,
            { reviewId: Long -> reviewRepo.findByReviewId(reviewId) },
            { NoReviewFoundException(reviewId) })
        doesExist(groupId,
            { reviewCommentId: Long -> reviewCommentRepo.findByReviewCommentId(reviewCommentId) },
            { NoReviewCommentFoundException(groupId) })

        val validData = ReviewCommentEntity(
            0L, reviewId, userId, childReviewComment.content, groupId, childReviewComment.commentRef, 0, null, false
        )

        return reviewCommentRepo.saveNewChildReviewComment(groupId, userId, validData)
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Throws(NoReviewCommentFoundException::class)
    override fun editReviewComment(
        reviewCommentId: Long, commentRef: Long?,
        contentReplacement: String
    ): ReviewCommentEntity? {
        val origin = doesExist(reviewCommentId,
            { reviewCommentId: Long -> reviewCommentRepo.findByReviewCommentId(reviewCommentId) },
            { NoReviewCommentFoundException(reviewCommentId) })

        if (commentRef != null) {

            origin!!.mentionReviewComment(
                (doesExist(commentRef,
                    { reviewCommentId: Long -> reviewCommentRepo.findByReviewCommentId(reviewCommentId) },
                    { NoReviewCommentFoundException(reviewCommentId) }))!!
            )
        }

        val replacement: ReviewCommentEntity = ReviewCommentEntity(
            content = contentReplacement,
            commentRef = origin!!.commentRef
        )

        return reviewCommentRepo.editReviewCommentInfo(reviewCommentId, replacement, true).orElseThrow {
            throw NoReviewCommentFoundException(
                reviewCommentId
            )
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Throws(NoReviewCommentFoundException::class)
    override fun deleteReviewComment(reviewCommentId: Long): ReviewCommentEntity? {
        doesExist(reviewCommentId,
            { reviewCommentId: Long -> reviewCommentRepo.findByReviewCommentId(reviewCommentId) },
            { NoReviewCommentFoundException(reviewCommentId) })

        val validData: ReviewCommentEntity = ReviewCommentEntity(
            content = "해당 댓글은 삭제되었습니다.",
            commentRef = null
        )
        return reviewCommentRepo.editReviewCommentInfo(reviewCommentId, validData, true).orElseThrow { throw NoReviewCommentFoundException(reviewCommentId) }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Throws(NoReviewCommentFoundException::class)
    override fun increaseCommentLike(reviewCommentId: Long): ReviewCommentEntity? {
        var likeOrigin = doesExist(reviewCommentId,
            { reviewCommentId: Long -> reviewCommentRepo.findByReviewCommentId(reviewCommentId) },
            { NoReviewCommentFoundException(reviewCommentId) })!!.like

        return reviewCommentRepo.updateReviewCommentLikes(reviewCommentId, ++likeOrigin).orElseThrow { throw NoReviewCommentFoundException(reviewCommentId) }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Throws(NoReviewCommentFoundException::class)
    override fun decreaseCommentLike(reviewCommentId: Long): ReviewCommentEntity? {
        var likeOrigin = doesExist(reviewCommentId,
            { reviewCommentId: Long -> reviewCommentRepo.findByReviewCommentId(reviewCommentId) },
            { NoReviewCommentFoundException(reviewCommentId) })!!.like

        return reviewCommentRepo.updateReviewCommentLikes(
            reviewCommentId,
            if (likeOrigin <= 0) 0 else --likeOrigin
        ).orElseThrow { throw NoReviewCommentFoundException(reviewCommentId) }
    }

    companion object {
        /**
         * 주어진 `id` 로 `function` 호출했을 때 값이 존재하는지 아닌지 확인하는 메서드
         *
         * @param id        검사할 ID
         * @param function  호출할 함수 `(Long -> Optional<?>`
         * @param exception 부재시 `throw` 할 exception `(() -> RuntimeException)`
         */
        private fun <R> doesExist(
            id: Long,
            function: Function<Long, Optional<R>>,
            exception: (Long) -> RuntimeException
        ): R {
            val applied = function.apply(id)
            if (applied.isEmpty) {
                throw exception(id)
            }
            return applied.get()
        }
    }
}
