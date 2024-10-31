package core.application.reviews.repositories.jpa.repositories

import core.application.reviews.models.entities.ReviewCommentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaReviewCommentRepository : JpaRepository<ReviewCommentEntity?, Long?> {
    @Query(
        value = (" SELECT * FROM review_comment_table "
                + " WHERE review_id = :reviewId AND group_id IS NULL "
                + " ORDER BY created_at DESC, review_comment_id DESC "
                + " LIMIT :num OFFSET :offset "), nativeQuery = true
    )
    fun findParentCommentByReviewId(
        reviewId: Long?,
        offset: Int, num: Int
    ): List<ReviewCommentEntity>

    @Query(
        value = (" SELECT * FROM review_comment_table "
                + " WHERE review_id = :reviewId AND group_id IS NULL "
                + " ORDER BY `like` DESC, review_comment_id DESC "
                + " LIMIT :num OFFSET :offset "), nativeQuery = true
    )
    fun findParentCommentByReviewIdOnLikeDescend(
        reviewId: Long?,
        offset: Int, num: Int
    ): List<ReviewCommentEntity>

    @Query(
        (" SELECT COUNT(*) FROM ReviewCommentEntity r "
                + " WHERE r.reviewId = :reviewId AND r.groupId IS NULL ")
    )
    fun countParentCommentByReviewId(reviewId: Long?): Long

    @Query(
        value = (" SELECT * FROM review_comment_table "
                + " WHERE group_id = :groupId "
                + " ORDER BY created_at DESC, review_comment_id DESC "
                + " LIMIT :num OFFSET :offset "), nativeQuery = true
    )
    fun findChildCommentsByGroupId(
        groupId: Long?,
        offset: Int, num: Int
    ): List<ReviewCommentEntity>

    @Query(
        (" SELECT COUNT(*) FROM ReviewCommentEntity r "
                + " WHERE r.groupId = :groupId ")
    )
    fun countChildCommentByGroupId(groupId: Long?): Long

    @Query(" SELECT r FROM ReviewCommentEntity r WHERE r.groupId IS NULL ")
    fun selectAllParentComments(): List<ReviewCommentEntity>
}
