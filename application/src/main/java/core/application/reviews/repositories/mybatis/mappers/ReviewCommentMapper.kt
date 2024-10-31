package core.application.reviews.repositories.mybatis.mappers

import core.application.reviews.models.entities.ReviewCommentEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.util.*

/**
 * `ReviewCommentRepository` 에 사용될 `MyBatis mapper`
 *
 * @see core.application.reviews.repositories.mybatis.MyBatisReviewCommentRepository
 *
 * @see core.application.reviews.repositories.ReviewCommentRepository
 */
@Mapper
interface ReviewCommentMapper {
    /**
     * 실질적으로 DB 에 `insert` 하는 `MyBatis Query` 용 메서드
     *
     * @param data 삽입 데이터
     * @return `insert` 결과
     */
    fun insertReviewComment(
        data: ReviewCommentEntity?
    ): Int

    /**
     * 포스팅 댓글 ID 로 검색
     *
     * @param reviewCommentId 포스팅 댓글 ID
     * @return [Optional]`<`[ReviewCommentEntity]`>`
     */
    fun findByReviewCommentId(reviewCommentId: Long?): Optional<ReviewCommentEntity?>

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
    fun findParentCommentByReviewId(
        @Param("reviewId") reviewId: Long?,
        @Param("offset") offset: Int,
        @Param("num") num: Int
    ): List<ReviewCommentEntity>

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
        @Param("reviewId") reviewId: Long?,
        @Param("offset") offset: Int,
        @Param("num") num: Int
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
        @Param("reviewId") reviewId: Long?,
        @Param("offset") offset: Int,
        @Param("num") num: Int
    ): List<ReviewCommentEntity>

    /**
     * 특정 포스팅에 달린 모든 부모 댓글의 개수를 확인
     *
     * @param reviewId 검색할 포스팅 ID
     * @return 부모 댓글의 개수
     */
    fun countParentCommentByReviewId(reviewId: Long?): Long

    /**
     * 특정 부모 댓글에 달린 자식 댓글들을 최신순으로 검색 (페이징)
     *
     *
     * 자식 댓글은 `groupId != null` 인 댓글들.
     *
     * @param groupId 부모 댓글의 ID
     * @return [List]`<`[ReviewCommentEntity]`>`
     */
    fun findChildCommentsByGroupId(
        @Param("groupId") groupId: Long?,
        @Param("offset") offset: Int,
        @Param("num") num: Int
    ): List<ReviewCommentEntity>

    /**
     * 특정 부모 댓글 아래 자식 댓글의 개수를 확인
     *
     * @param groupId 부모 댓글 ID
     * @return 자식 댓글의 개수
     */
    fun countChildCommentByGroupId(groupId: Long?): Long

    /**
     * DB 에 저장된 모든 부모 포스팅 댓글을 검색
     *
     *
     * 즉, `groupId == null` 인 댓글만 검색.
     *
     * @return [List]`<`[ReviewCommentEntity]`>`
     */
    fun selectAllParentComments(): List<ReviewCommentEntity>

    /**
     * DB 에 저장된 모든 포스팅 댓글을 검색
     *
     *
     * 자식 댓글은 `groupId != null` 인 댓글들.
     *
     * @return [List]`<`[ReviewCommentEntity]`>`
     */
    fun selectAll(): List<ReviewCommentEntity>

    /**
     * 실질적으로 DB 에 `update` 하는 `MyBatis Query` 용 메서드
     *
     * @param reviewCommentId 댓글 ID
     * @param replacement     변경할 정보
     * @param update          `is_updated` 에 설정할 정보
     * @return `update` 결과
     */
    fun updateReviewCommentEntity(
        @Param("reviewCommentId") reviewCommentId: Long?,
        @Param("replacement") replacement: ReviewCommentEntity?,
        @Param("isUpdated") update: Boolean
    ): Int

    /**
     * 실질적으로 DB 에 리뷰 좋아요 `update` 하는 `MyBatis Query` 용 메서드
     *
     * @param reviewCommentId 댓글 ID
     * @param likes           변경될 좋아요 수
     * @return `update` 결과
     */
    fun updateCommentLikes(
        @Param("reviewCommentId") reviewCommentId: Long?,
        @Param("likes") likes: Int
    ): Int
}
