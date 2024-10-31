package core.application.reviews.repositories

import core.application.reviews.models.entities.ReviewCommentEntity
import java.util.*


/**
 * `REVIEW_COMMENT_TABLE` 과 관련된 `Repository`
 */
interface ReviewCommentRepository {
    //<editor-fold desc="CREATE">
    /**
     * 새로운 포스팅 댓글을 DB 에 등록
     *
     * @param reviewComment 등록할 포스팅 댓글 정보
     * @return [ReviewCommentEntity] 등록된 정보
     */
    fun saveNewReviewComment(reviewComment: ReviewCommentEntity): ReviewCommentEntity?

    // 부모 댓글을 등록
    /**
     * 특정 포스팅에 주어진 유저 ID 로 부모 댓글을 등록
     *
     * @param reviewId      댓글을 등록할 영화 후기 포스팅 ID
     * @param userId        포스팅 댓글을 등록하는 유저 ID
     * @param reviewComment 등록할 포스팅 댓글 정보
     * @return [ReviewCommentEntity] 등록된 정보
     */
    fun saveNewParentReviewComment(
        reviewId: Long, userId: UUID,
        reviewComment: ReviewCommentEntity
    ): ReviewCommentEntity?

    // 자식 댓글을 등록
    /**
     * 특정 포스팅 댓글에 주어진 유저 ID 로 자식 댓글을 등록
     *
     * @param groupId       부모 댓글의 포스팅 댓글 ID
     * @param userId        댓글을 등록하는 유저 ID
     * @param reviewComment 등록할 댓글 정보
     * @return [ReviewCommentEntity] 등록된 정보
     */
    fun saveNewChildReviewComment(
        groupId: Long, userId: UUID,
        reviewComment: ReviewCommentEntity
    ): ReviewCommentEntity?

    //</editor-fold>
    //<editor-fold desc="READ">
    /**
     * 포스팅 댓글 ID 로 검색
     *
     * @param reviewCommentId 포스팅 댓글 ID
     * @return [Optional]`<`[ReviewCommentEntity]`>`
     */
    fun findByReviewCommentId(reviewCommentId: Long): Optional<ReviewCommentEntity?>

    //<editor-fold desc="부모 댓글 검색">
    /**
     * 특정 포스팅에 달린 모든 부모 댓글을 검색 (페이징)
     *
     *
     * 즉, `groupId == null` 인 댓글만 검색.
     *
     * @param reviewId 검색할 포스팅 ID
     * @param offset   댓글 offset
     * @param num      가져올 댓글 수
     * @return [List]`<`[ReviewCommentEntity]`>`
     */
    fun findParentCommentByReviewId(reviewId: Long, offset: Int, num: Int): List<ReviewCommentEntity?>?

    /**
     * 특정 포스팅에 달린 모든 부모 댓글을 최신순으로 검색 (페이징)
     *
     *
     * 즉, `groupId == null` 인 댓글만 검색.
     *
     * @param reviewId 검색할 포스팅 ID
     * @param offset   오프셋
     * @param num      가져올 개수
     * @return [List]`<`[ReviewCommentEntity]`>`
     */
    fun findParentCommentByReviewIdOnDateDescend(
        reviewId: Long, offset: Int,
        num: Int
    ): List<ReviewCommentEntity>

    /**
     * 특정 포스팅에 달린 모든 부모 댓글을 좋아요 순으로 검색 (페이징)
     *
     *
     * 즉, `groupId == null` 인 댓글만 검색.
     *
     * @param reviewId 검색할 포스팅 ID
     * @param offset   오프셋
     * @param num      가져올 개수
     * @return [List]`<`[ReviewCommentEntity]`>`
     */
    fun findParentCommentByReviewIdOnLikeDescend(
        reviewId: Long, offset: Int,
        num: Int
    ): List<ReviewCommentEntity>

    /**
     * 특정 포스팅에 달린 모든 부모 댓글의 개수를 확인
     *
     * @param reviewId 검색할 포스팅 ID
     * @return 부모 댓글의 개수
     */
    fun countParentCommentByReviewId(reviewId: Long): Long


    //</editor-fold>
    /**
     * 특정 부모 댓글에 달린 자식 댓글들을 최신순으로 검색 (페이징)
     *
     *
     * 자식 댓글은 `groupId != null` 인 댓글들.
     *
     * @param groupId 부모 댓글의 ID
     * @return [List]`<`[ReviewCommentEntity]`>`
     */
    fun findChildCommentsByGroupId(groupId: Long, offset: Int, num: Int): List<ReviewCommentEntity>

    /**
     * 특정 부모 댓글 아래 자식 댓글의 개수를 확인
     *
     * @param groupId 부모 댓글 ID
     * @return 자식 댓글의 개수
     */
    fun countChildCommentByGroupId(groupId: Long): Long


    /**
     * DB 에 저장된 모든 부모 포스팅 댓글을 검색
     *
     *
     * 즉, `groupId == null` 인 댓글만 검색.
     *
     * @return [List]`<`[ReviewCommentEntity]`>`
     */
    fun selectAllParentComments(): List<ReviewCommentEntity?>?

    /**
     * DB 에 저장된 모든 포스팅 댓글을 검색
     *
     *
     * 자식 댓글은 `groupId != null` 인 댓글들.
     *
     * @return [List]`<`[ReviewCommentEntity]`>`
     */
    fun selectAll(): List<ReviewCommentEntity?>?

    //</editor-fold>
    // UPDATE
    /**
     * 특정 포스팅 댓글의 정보를 `replacement` 정보로 변경
     *
     *
     * 이 때 `content`, `commentRef` 만 `replacement` 의 것으로 변경. `isUpdated` 는
     * 자동으로 변경.
     *
     * @param reviewCommentId 정보 변경할 포스팅 댓글의 ID
     * @param replacement     변경할 정보
     * @return [ReviewCommentEntity] 변경된 정보
     */
    @Deprecated("use instead {@link #editReviewCommentInfo(Long, ReviewCommentEntity, boolean)}")
    fun editReviewCommentInfo(
        reviewCommentId: Long,
        replacement: ReviewCommentEntity
    ): Optional<ReviewCommentEntity?>

    /**
     * 특정 포스팅 댓글의 정보를 `replacement` 정보로 변경
     *
     *
     * 이 때 `content`, `commentRef` 만 `replacement` 의 것으로 변경. `isUpdated` 는
     * 주어진 대로 변경.
     *
     * @param reviewCommentId 정보 변경할 포스팅 댓글의 ID
     * @param replacement     변경할 정보
     * @param update          `is_updated` 에 설정할 정보
     * @return [ReviewCommentEntity] 변경된 정보
     */
    fun editReviewCommentInfo(
        reviewCommentId: Long, replacement: ReviewCommentEntity,
        update: Boolean
    ): Optional<ReviewCommentEntity?>

    /**
     * 특정 포스팅 댓글의 좋아요를 수정
     *
     * @param reviewCommentId 수정할 댓글 ID
     * @param likes           설정할 좋아요 수
     * @return [ReviewCommentEntity] 변경된 정보
     */
    fun updateReviewCommentLikes(reviewCommentId: Long, likes: Int): Optional<ReviewCommentEntity?>
}
