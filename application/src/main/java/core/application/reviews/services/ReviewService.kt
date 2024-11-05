package core.application.reviews.services

import core.application.movies.exception.NoMovieException
import core.application.reviews.exceptions.NoReviewFoundException
import core.application.reviews.models.entities.ReviewEntity
import java.util.*

/**
 * 영화 후기 포스팅과 관련된 서비스 인터페이스
 */
interface ReviewService {
    /**
     * 특정 영화에 달린 리뷰 포스팅 목록을 보여주는 서비스
     *
     * @param movieId     검색할 영화 ID
     * @param order       리뷰 포스팅 정렬 순서 `(최신순, 좋아요순)`
     * @param withContent 본문을 포함해서 불러올지 `Y/N`
     * @param offset      페이징 `offset`
     * @param num         가져올 포스팅 개수
     * @return 리뷰 포스팅 목록
     * @throws NoMovieException 영화 ID 에 해당하는 영화가 DB 에 존재하지 않을 시
     */
    @Throws(NoMovieException::class)
    fun getReviewsOnMovieId(
        movieId: String, order: ReviewSortOrder,
        withContent: Boolean, offset: Int, num: Int
    ): List<ReviewEntity>

    /**
     * 특정 영화에 달린 리뷰 포스팅의 총 개수를 보여주는 서비스
     *
     * @param movieId 검색할 영화 ID
     * @return 리뷰 포스팅 총 개수
     * @throws NoMovieException 영화 ID 에 해당하는 영화가 DB 에 존재하지 않을 시
     */
    @Throws(NoMovieException::class)
    fun getNumberOfReviewsOnMovieId(movieId: String): Long

    /**
     * 새로운 리뷰 포스팅을 생성하는 서비스
     *
     * @param movieId 포스팅을 작성할 영화 ID
     * @param userId  포스팅을 작성하는 사용자 ID
     * @param title   포스팅 제목
     * @param content 포스팅 본문
     * @return 생성된 포스팅 정보
     */
    @Throws(NoMovieException::class)
    fun createNewReview(movieId: String, userId: UUID, title: String, content: String): ReviewEntity?

    /**
     * 한 리뷰의 상세 정보를 가져오는 서비스
     *
     * @param reviewId    리뷰 포스팅 ID
     * @param withContent 본문을 포함해서 불러올지 `Y/N`
     * @return [Optional]`<`[ReviewEntity]`>`
     * @throws NoReviewFoundException `reviewId` 에 해당하는 리뷰 포스팅을 찾지 못했을 시
     * @author semin9809
     * @see ReviewSortOrder
     */
    @Throws(NoReviewFoundException::class)
    fun getReviewInfo(reviewId: Long, withContent: Boolean): ReviewEntity


    /**
     * 특정 리뷰 포스팅을 수정하는 서비스
     *
     * @param reviewId     리뷰 포스팅 ID
     * @param updateReview 수정된 리뷰
     * @return [ReviewEntity]
     * @throws NoReviewFoundException `reviewId` 에 해당하는 리뷰 포스팅을 찾지 못했을 시
     * @author semin9809
     */
    @Throws(NoReviewFoundException::class)
    fun updateReviewInfo(reviewId: Long, updateReview: ReviewEntity): ReviewEntity?


    /**
     * 리뷰 삭제하는 서비스
     *
     * @param reviewId 리뷰 포스팅 ID
     * @return [ReviewEntity] 삭제된 리뷰 정보
     * @throws NoReviewFoundException `reviewId` 에 해당하는 리뷰 포스팅을 찾지 못했을 시
     * @author semin9809
     */
    @Throws(NoReviewFoundException::class)
    fun deleteReview(reviewId: Long): ReviewEntity?


    /**
     * 리뷰에 좋아요 누르기
     *
     *
     * userId로 확인하여 중복 좋아요 방지
     *
     * @param reviewId 리뷰 포스팅 ID
     * @return [ReviewEntity] 좋아요가 1 증가된 리뷰 정보
     * @throws NoReviewFoundException `reviewId` 에 해당하는 리뷰 포스팅을 찾지 못했을 시
     * @author semin9809
     */
    @Throws(NoReviewFoundException::class)
    fun increaseLikes(reviewId: Long): ReviewEntity?

    /**
     * 리뷰에 누른 좋아요 취소하기
     *
     *
     * userId로 확인하여 중복 처리 못하게 하기
     *
     * @param reviewId 리뷰 포스팅 ID
     * @return [ReviewEntity] 좋아요가 1 감소된 리뷰 정보
     * @throws NoReviewFoundException `reviewId` 에 해당하는 리뷰 포스팅을 찾지 못했을 시
     * @author semin9809
     */
    @Throws(NoReviewFoundException::class)
    fun decreaseLikes(reviewId: Long): ReviewEntity?

    /**
     * 주어진 `movieId` 에 해당하는 영화가 존재하는지 확인하는 서비스
     *
     * @param movieId 영화 ID
     * @throws NoMovieException 영화가 DB 에 존재하지 않을 시
     */
    @Throws(NoMovieException::class)
    fun checkWhetherMovieExist(movieId: String)
}
