package core.application.reviews.services;

import core.application.movies.exception.*;
import core.application.reviews.exceptions.*;
import core.application.reviews.models.entities.*;
import java.util.*;

/**
 * 영화 후기 포스팅과 관련된 서비스 인터페이스
 */
public interface ReviewService {

    /**
     * 특정 영화에 달린 리뷰 포스팅 목록을 보여주는 서비스
     *
     * @param movieId     검색할 영화 ID
     * @param order       리뷰 포스팅 정렬 순서 {@code (최신순, 좋아요순)}
     * @param withContent 본문을 포함해서 불러올지 {@code Y/N}
     * @param offset      페이징 {@code offset}
     * @param num         가져올 포스팅 개수
     * @return 리뷰 포스팅 목록
     * @throws NoMovieException 영화 ID 에 해당하는 영화가 DB 에 존재하지 않을 시
     */
    List<ReviewEntity> getReviewsOnMovieId(String movieId, ReviewSortOrder order,
            boolean withContent, int offset, int num) throws NoMovieException;

    /**
     * 특정 영화에 달린 리뷰 포스팅의 총 개수를 보여주는 서비스
     *
     * @param movieId 검색할 영화 ID
     * @return 리뷰 포스팅 총 개수
     * @throws NoMovieException 영화 ID 에 해당하는 영화가 DB 에 존재하지 않을 시
     */
    long getNumberOfReviewsOnMovieId(String movieId) throws NoMovieException;

    /**
     * 새로운 리뷰 포스팅을 생성하는 서비스
     *
     * @param movieId 포스팅을 작성할 영화 ID
     * @param userId  포스팅을 작성하는 사용자 ID
     * @param title   포스팅 제목
     * @param content 포스팅 본문
     * @return 생성된 포스팅 정보
     */
    ReviewEntity createNewReview(String movieId, UUID userId, String title, String content)
            throws NoMovieException;

    /**
     * 한 리뷰의 상세 정보를 가져오는 서비스
     *
     * @param reviewId    리뷰 포스팅 ID
     * @param withContent 본문을 포함해서 불러올지 {@code Y/N}
     * @return {@link Optional}{@code <}{@link ReviewEntity}{@code >}
     * @throws NoReviewFoundException {@code reviewId} 에 해당하는 리뷰 포스팅을 찾지 못했을 시
     * @author semin9809
     * @see ReviewSortOrder
     */
    ReviewEntity getReviewInfo(Long reviewId, boolean withContent)
            throws NoReviewFoundException;


    /**
     * 특정 리뷰 포스팅을 수정하는 서비스
     *
     * @param reviewId     리뷰 포스팅 ID
     * @param updateReview 수정된 리뷰
     * @return {@link ReviewEntity}
     * @throws NoReviewFoundException {@code reviewId} 에 해당하는 리뷰 포스팅을 찾지 못했을 시
     * @author semin9809
     */
    ReviewEntity updateReviewInfo(Long reviewId, ReviewEntity updateReview)
            throws NoReviewFoundException;


    /**
     * 리뷰 삭제하는 서비스
     *
     * @param reviewId 리뷰 포스팅 ID
     * @return {@link ReviewEntity} 삭제된 리뷰 정보
     * @throws NoReviewFoundException {@code reviewId} 에 해당하는 리뷰 포스팅을 찾지 못했을 시
     * @author semin9809
     */
    ReviewEntity deleteReview(Long reviewId) throws NoReviewFoundException;


    /**
     * 리뷰에 좋아요 누르기
     * <p>
     * userId로 확인하여 중복 좋아요 방지
     *
     * @param reviewId 리뷰 포스팅 ID
     * @return {@link ReviewEntity} 좋아요가 1 증가된 리뷰 정보
     * @throws NoReviewFoundException {@code reviewId} 에 해당하는 리뷰 포스팅을 찾지 못했을 시
     * @author semin9809
     */
    ReviewEntity increaseLikes(Long reviewId) throws NoReviewFoundException;

    /**
     * 리뷰에 누른 좋아요 취소하기
     * <p>
     * userId로 확인하여 중복 처리 못하게 하기
     *
     * @param reviewId 리뷰 포스팅 ID
     * @return {@link ReviewEntity} 좋아요가 1 감소된 리뷰 정보
     * @throws NoReviewFoundException {@code reviewId} 에 해당하는 리뷰 포스팅을 찾지 못했을 시
     * @author semin9809
     */
    ReviewEntity decreaseLikes(Long reviewId) throws NoReviewFoundException;

    /**
     * 주어진 {@code movieId} 에 해당하는 영화가 존재하는지 확인하는 서비스
     *
     * @param movieId 영화 ID
     * @throws NoMovieException 영화가 DB 에 존재하지 않을 시
     */
    void checkWhetherMovieExist(String movieId) throws NoMovieException;
}
