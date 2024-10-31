package core.application.reviews.repositories

import core.application.reviews.models.entities.ReviewEntity
import java.util.*


/**
 * `REVIEW_TABLE` 과 관련된 `Repository`
 */
interface ReviewRepository {
    // CREATE
    /**
     * 특정 영화에 주어진 유저 ID 로 새로운 영화 후기 포스팅을 DB 에 등록
     *
     * @param movieId 후기 포스팅을 등록할 영화 ID
     * @param userId  포스팅을 등록하는 유저 ID
     * @param review  새로운 영화 후기 포스팅
     * @return [ReviewEntity] 등록된 정보
     */
    fun saveNewReview(movieId: String, userId: UUID, review: ReviewEntity): ReviewEntity

    //<editor-fold desc="READ">
    /**
     * 후기 포스팅 ID 로 검색
     *
     * @param reviewId 후기 포스팅 ID
     * @return [Optional]`<`[ReviewEntity]`>`
     */
    fun findByReviewId(reviewId: Long): Optional<ReviewEntity?>

    /**
     * 후기 포스팅 ID 로 검색 `(본문 없이 가져오기)`
     *
     * @param reviewId 후기 포스팅 ID
     * @return [Optional]`<`[ReviewEntity]`>`
     */
    fun findByReviewIdWithoutContent(reviewId: Long): Optional<ReviewEntity?>

    //<editor-fold desc="특정 영화의 후기 포스팅들을 검색">
    /**
     * 특정 영화의 후기 포스팅들을 검색
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 `offset`
     * @param num     가져올 포스팅 수
     * @return [List]`<`[ReviewEntity]`>`
     */
    fun findByMovieId(movieId: String, offset: Int, num: Int): List<ReviewEntity?>?

    /**
     * 특정 영화의 후기 포스팅들을 최신순으로 검색
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 `offset`
     * @param num     가져올 포스팅 수
     * @return [List]`<`[ReviewEntity]`>`
     */
    fun findByMovieIdOnDateDescend(movieId: String, offset: Int, num: Int): List<ReviewEntity>

    /**
     * 특정 영화의 후기 포스팅들을 좋아요 순으로 검색
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 `offset`
     * @param num     가져올 포스팅 수
     * @return [List]`<`[ReviewEntity]`>`
     */
    fun findByMovieIdOnLikeDescend(movieId: String, offset: Int, num: Int): List<ReviewEntity>

    //</editor-fold>
    //<editor-fold desc="특정 영화의 포스팅을 본문 없이 검색">
    /**
     * 특정 영화의 후기 포스팅들을 검색
     *
     *
     * 이 때 포스팅의 본문을 load 하지 않음.
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 `offset`
     * @param num     가져올 포스팅 수
     * @return [List]`<`[ReviewEntity]`>`
     */
    fun findByMovieIdWithoutContent(movieId: String, offset: Int, num: Int): List<ReviewEntity>

    /**
     * 특정 영화의 후기 포스팅들을 최신순으로 검색
     *
     *
     * 이 때 포스팅의 본문을 load 하지 않음.
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 `offset`
     * @param num     가져올 포스팅 수
     * @return [List]`<`[ReviewEntity]`>`
     */
    fun findByMovieIdWithoutContentOnDateDescend(
        movieId: String, offset: Int,
        num: Int
    ): List<ReviewEntity>

    /**
     * 특정 영화의 후기 포스팅들을 좋아요 순으로 검색
     *
     *
     * 이 때 포스팅의 본문을 load 하지 않음.
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 `offset`
     * @param num     가져올 포스팅 수
     * @return [List]`<`[ReviewEntity]`>`
     */
    fun findByMovieIdWithoutContentOnLikeDescend(
        movieId: String, offset: Int,
        num: Int
    ): List<ReviewEntity>

    /**
     * 특정 영화의 후기 포스팅들 개수를 검색
     *
     * @param movieId 검색할 영화 ID
     * @return 영화에 달린 포스팅 총 개수
     */
    fun countByMovieId(movieId: String): Long

    //</editor-fold>
    /**
     * 특정 유저가 작성한 영화 후기 포스팅들을 검색
     *
     * @param userId 검색할 유저 ID
     * @return [List]`<`[ReviewEntity]`>`
     */
    fun findByUserId(userId: UUID): List<ReviewEntity>

    /**
     * DB 의 모든 영화 후기 포스팅을 검색
     *
     * @return [List]`<`[ReviewEntity]`>`
     */
    fun selectAll(): List<ReviewEntity?>

    //</editor-fold>
    // UPDATE
    /**
     * 특정 후기 포스팅의 정보를 `replacement` 정보로 변경.
     *
     *
     * 이 때 `title`, `content` 만 `replacement` 의 것으로 변경. `updatedAt` 은 자동으로
     * 변경.
     *
     * @param reviewId    정보 변경할 포스팅의 ID
     * @param replacement 변경할 정보
     * @return [ReviewEntity] 변경된 정보
     */
    fun editReviewInfo(reviewId: Long, replacement: ReviewEntity): ReviewEntity?

    /**
     * 특정 후기 포스팅에 좋아요를 `likes` 값으로 재설정
     *
     * @param reviewId   변경할 포스팅의 ID
     * @param givenLikes 변경할 좋아요 값
     * @return [ReviewEntity] 변경된 정보
     */
    fun updateReviewLikes(reviewId: Long, givenLikes: Int): ReviewEntity?

    // DELETE
    /**
     * 특정 후기 포스팅을 삭제
     *
     * @param reviewId 삭제할 포스팅 ID
     */
    fun deleteReview(reviewId: Long)
}
