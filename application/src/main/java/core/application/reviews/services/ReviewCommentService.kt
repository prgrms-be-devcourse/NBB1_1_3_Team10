package core.application.reviews.services

import core.application.reviews.exceptions.NoReviewCommentFoundException
import core.application.reviews.exceptions.NoReviewFoundException
import core.application.reviews.models.entities.ReviewCommentEntity
import java.util.*

/**
 * 영화 후기 포스팅 댓글과 관련된 서비스
 */
interface ReviewCommentService {
    /**
     * 특정 포스팅 내 부모 댓글의 개수를 조회하는 서비스
     *
     * @param reviewId 영화 후기 포스팅 ID
     * @return 부모 댓글의 개수
     */
    @Throws(NoReviewFoundException::class)
    fun getNumberOfParentComment(reviewId: Long): Long

    /**
     * 특정 부모 댓글 아래 자식 댓글의 개수를 조회하는 서비스
     *
     * @param groupId 부모 댓글 ID
     * @return 자식 댓글의 개수
     */
    @Throws(NoReviewCommentFoundException::class)
    fun getNumberOfChildComment(groupId: Long): Long

    @Throws(NoReviewCommentFoundException::class)
    fun doesUserOwnsComment(userId: UUID, reviewCommentId: Long): Boolean

    /**
     * 특정 리뷰 포스팅의 부모 댓글을 불러오는 서비스
     *
     * @param reviewId 리뷰 포스팅 ID
     * @param order    보모 댓글 정렬 순서 `(최신순, 좋아요순)`
     * @param offset   댓글 offset
     * @param num      가져올 댓글의 개수
     * @return [List]`<`[ReviewCommentEntity]`>`
     * @throws NoReviewFoundException `reviewId` 에 해당하는 리뷰를 찾지 못했을 시
     * @author jbw9964
     * @see ReviewCommentSortOrder
     */
    @Throws(NoReviewFoundException::class)
    fun getParentReviewComments(
        reviewId: Long, order: ReviewCommentSortOrder,
        offset: Int, num: Int
    ): List<ReviewCommentEntity>

    /**
     * 특정 부모 댓글의 자식 댓글을 불러오는 서비스
     *
     *
     * 이 때 불러오는 자식 댓글은 최신순
     *
     * @param reviewId 댓글이 달리는 포스팅 ID
     * @param groupId  부모 댓글의 ID
     * @param offset   댓글 offset
     * @param num      가져올 댓글 개수
     * @return [List]`<`[ReviewCommentEntity]`>`
     * @author jbw9964
     */
    fun getChildReviewCommentsOnParent(
        reviewId: Long, groupId: Long,
        offset: Int, num: Int
    ): List<ReviewCommentEntity>

    /**
     * 특정 포스팅에 부모 댓글 다는 서비스
     *
     * @param reviewId            포스팅 ID
     * @param userId              댓글 작성하는 유저 ID
     * @param parentReviewComment 댓글 정보
     * @return [ReviewCommentEntity] 등록된 댓글 정보
     * @throws NoReviewFoundException `reviewId` 에 해당하는 리뷰를 찾지 못했을 시
     * @author jbw9964
     */
    @Throws(NoReviewFoundException::class)
    fun addNewParentReviewComment(
        reviewId: Long, userId: UUID,
        parentReviewComment: ReviewCommentEntity
    ): ReviewCommentEntity?

    /**
     * 특정 포스팅 내 부모 댓글에 자식 댓글 다는 서비스
     *
     * @param reviewId           댓글이 작성되는 포스팅 ID
     * @param groupId            부모 댓글 ID
     * @param userId             댓글 작성하는 유저 ID
     * @param childReviewComment 자식 댓글 정보
     * @return [ReviewCommentEntity] 등록된 댓글 정보
     * @throws NoReviewFoundException        `reviewId` 에 해당하는 리뷰를 찾지 못했을 시
     * @throws NoReviewCommentFoundException `reviewId` 에 해당하는 부모 댓글을 찾지 못했을 시
     * @author jbw9964
     */
    @Throws(NoReviewFoundException::class, NoReviewCommentFoundException::class)
    fun addNewChildReviewComment(
        reviewId: Long, groupId: Long, userId: UUID, childReviewComment: ReviewCommentEntity
    ): ReviewCommentEntity?

    /**
     * 특정 댓글의 내용을 수정하는 서비스
     *
     * @param reviewCommentId    수정할 댓글 ID
     * @param commentRef         `(필요하다면)` 멘션할 댓글 ID
     * @param contentReplacement 수정할 댓글 내용
     * @return [ReviewCommentEntity] 수정된 정보
     * @throws NoReviewCommentFoundException `reviewCommentId` 에 해당하는 댓글을 찾지 못했을 시
     * @author jbw9964
     */
    @Throws(NoReviewCommentFoundException::class)
    fun editReviewComment(
        reviewCommentId: Long, commentRef: Long?,
        contentReplacement: String
    ): ReviewCommentEntity?

    /**
     * 특정 댓글을 삭제하는 서비스
     *
     *
     * 이 때 DB 에 정말로 삭제되는게 아닌 `content` 가 `(댓글이 삭제되었습니다.)` 로 변경됨.
     *
     * @param reviewCommentId 삭제할 댓글 ID
     * @return [ReviewCommentEntity] 삭제된 된 댓글 정보
     * @throws NoReviewCommentFoundException `reviewCommentId` 에 해당하는 댓글을 찾지 못했을 시
     * @author jbw9964
     */
    @Throws(NoReviewCommentFoundException::class)
    fun deleteReviewComment(reviewCommentId: Long): ReviewCommentEntity?

    /**
     * 특정 댓글의 좋아요를 1 증가시키는 서비스
     *
     * @param reviewCommentId 좋아요 누를 댓글 ID
     * @return [ReviewCommentEntity] 좋아요가 1 증가한 댓글 정보
     * @throws NoReviewCommentFoundException `reviewCommentId` 에 해당하는 댓글을 찾지 못했을 시
     * @author jbw9964
     */
    @Throws(NoReviewCommentFoundException::class)
    fun increaseCommentLike(reviewCommentId: Long): ReviewCommentEntity?

    /**
     * 특정 댓글의 좋아요를 1 감소시키는 서비스
     *
     * @param reviewCommentId 좋아요 취소할 댓글 ID
     * @return [ReviewCommentEntity] 좋아요가 1 감소된 댓글 정보
     * @throws NoReviewCommentFoundException `reviewCommentId` 에 해당하는 댓글을 찾지 못했을 시
     * @author jbw9964
     */
    @Throws(NoReviewCommentFoundException::class)
    fun decreaseCommentLike(reviewCommentId: Long): ReviewCommentEntity?
}
