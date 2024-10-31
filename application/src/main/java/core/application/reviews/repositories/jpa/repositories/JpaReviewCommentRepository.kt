package core.application.reviews.repositories.jpa.repositories;

import core.application.reviews.models.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface JpaReviewCommentRepository extends JpaRepository<ReviewCommentEntity, Long> {

    @Query(value = " SELECT * FROM review_comment_table "
            + " WHERE review_id = :reviewId AND group_id IS NULL "
            + " ORDER BY created_at DESC, review_comment_id DESC "
            + " LIMIT :num OFFSET :offset ", nativeQuery = true)
    List<ReviewCommentEntity> findParentCommentByReviewId(Long reviewId,
            int offset, int num);

    @Query(value = " SELECT * FROM review_comment_table "
            + " WHERE review_id = :reviewId AND group_id IS NULL "
            + " ORDER BY `like` DESC, review_comment_id DESC "
            + " LIMIT :num OFFSET :offset ", nativeQuery = true)
    List<ReviewCommentEntity> findParentCommentByReviewIdOnLikeDescend(Long reviewId,
            int offset, int num);

    @Query(" SELECT COUNT(*) FROM ReviewCommentEntity r "
            + " WHERE r.reviewId = :reviewId AND r.groupId IS NULL ")
    long countParentCommentByReviewId(Long reviewId);

    @Query(value = " SELECT * FROM review_comment_table "
            + " WHERE group_id = :groupId "
            + " ORDER BY created_at DESC, review_comment_id DESC "
            + " LIMIT :num OFFSET :offset ", nativeQuery = true)
    List<ReviewCommentEntity> findChildCommentsByGroupId(Long groupId,
            int offset, int num);

    @Query(" SELECT COUNT(*) FROM ReviewCommentEntity r "
            + " WHERE r.groupId = :groupId ")
    long countChildCommentByGroupId(Long groupId);

    @Query(" SELECT r FROM ReviewCommentEntity r WHERE r.groupId IS NULL ")
    List<ReviewCommentEntity> selectAllParentComments();
}
