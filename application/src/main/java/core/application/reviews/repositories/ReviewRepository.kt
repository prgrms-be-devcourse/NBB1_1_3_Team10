package core.application.reviews.repositories;


import core.application.reviews.models.entities.*;
import java.util.*;

/**
 * {@code REVIEW_TABLE} 과 관련된 {@code Repository}
 */
public interface ReviewRepository {

    // CREATE

    /**
     * 특정 영화에 주어진 유저 ID 로 새로운 영화 후기 포스팅을 DB 에 등록
     *
     * @param movieId 후기 포스팅을 등록할 영화 ID
     * @param userId  포스팅을 등록하는 유저 ID
     * @param review  새로운 영화 후기 포스팅
     * @return {@link ReviewEntity} 등록된 정보
     */
    ReviewEntity saveNewReview(String movieId, UUID userId, ReviewEntity review);

    //<editor-fold desc="READ">

    /**
     * 후기 포스팅 ID 로 검색
     *
     * @param reviewId 후기 포스팅 ID
     * @return {@link Optional}{@code <}{@link ReviewEntity}{@code >}
     */
    Optional<ReviewEntity> findByReviewId(Long reviewId);

    /**
     * 후기 포스팅 ID 로 검색 {@code (본문 없이 가져오기)}
     *
     * @param reviewId 후기 포스팅 ID
     * @return {@link Optional}{@code <}{@link ReviewEntity}{@code >}
     */
    Optional<ReviewEntity> findByReviewIdWithoutContent(Long reviewId);

    //<editor-fold desc="특정 영화의 후기 포스팅들을 검색">

    /**
     * 특정 영화의 후기 포스팅들을 검색
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 {@code offset}
     * @param num     가져올 포스팅 수
     * @return {@link List}{@code <}{@link ReviewEntity}{@code >}
     */
    List<ReviewEntity> findByMovieId(String movieId, int offset, int num);

    /**
     * 특정 영화의 후기 포스팅들을 최신순으로 검색
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 {@code offset}
     * @param num     가져올 포스팅 수
     * @return {@link List}{@code <}{@link ReviewEntity}{@code >}
     */
    List<ReviewEntity> findByMovieIdOnDateDescend(String movieId, int offset, int num);

    /**
     * 특정 영화의 후기 포스팅들을 좋아요 순으로 검색
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 {@code offset}
     * @param num     가져올 포스팅 수
     * @return {@link List}{@code <}{@link ReviewEntity}{@code >}
     */
    List<ReviewEntity> findByMovieIdOnLikeDescend(String movieId, int offset, int num);
    //</editor-fold>

    //<editor-fold desc="특정 영화의 포스팅을 본문 없이 검색">

    /**
     * 특정 영화의 후기 포스팅들을 검색
     * <p>
     * 이 때 포스팅의 본문을 load 하지 않음.
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 {@code offset}
     * @param num     가져올 포스팅 수
     * @return {@link List}{@code <}{@link ReviewEntity}{@code >}
     */
    List<ReviewEntity> findByMovieIdWithoutContent(String movieId, int offset, int num);

    /**
     * 특정 영화의 후기 포스팅들을 최신순으로 검색
     * <p>
     * 이 때 포스팅의 본문을 load 하지 않음.
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 {@code offset}
     * @param num     가져올 포스팅 수
     * @return {@link List}{@code <}{@link ReviewEntity}{@code >}
     */
    List<ReviewEntity> findByMovieIdWithoutContentOnDateDescend(String movieId, int offset,
            int num);

    /**
     * 특정 영화의 후기 포스팅들을 좋아요 순으로 검색
     * <p>
     * 이 때 포스팅의 본문을 load 하지 않음.
     *
     * @param movieId 검색할 영화 ID
     * @param offset  페이징 {@code offset}
     * @param num     가져올 포스팅 수
     * @return {@link List}{@code <}{@link ReviewEntity}{@code >}
     */
    List<ReviewEntity> findByMovieIdWithoutContentOnLikeDescend(String movieId, int offset,
            int num);

    /**
     * 특정 영화의 후기 포스팅들 개수를 검색
     *
     * @param movieId 검색할 영화 ID
     * @return 영화에 달린 포스팅 총 개수
     */
    Long countByMovieId(String movieId);
    //</editor-fold>

    /**
     * 특정 유저가 작성한 영화 후기 포스팅들을 검색
     *
     * @param userId 검색할 유저 ID
     * @return {@link List}{@code <}{@link ReviewEntity}{@code >}
     */
    List<ReviewEntity> findByUserId(UUID userId);

    /**
     * DB 의 모든 영화 후기 포스팅을 검색
     *
     * @return {@link List}{@code <}{@link ReviewEntity}{@code >}
     */
    List<ReviewEntity> selectAll();
    //</editor-fold>

    // UPDATE

    /**
     * 특정 후기 포스팅의 정보를 {@code replacement} 정보로 변경.
     * <p>
     * 이 때 {@code title}, {@code content} 만 {@code replacement} 의 것으로 변경. {@code updatedAt} 은 자동으로
     * 변경.
     *
     * @param reviewId    정보 변경할 포스팅의 ID
     * @param replacement 변경할 정보
     * @return {@link ReviewEntity} 변경된 정보
     */
    ReviewEntity editReviewInfo(Long reviewId, ReviewEntity replacement);

    /**
     * 특정 후기 포스팅에 좋아요를 {@code likes} 값으로 재설정
     *
     * @param reviewId   변경할 포스팅의 ID
     * @param givenLikes 변경할 좋아요 값
     * @return {@link ReviewEntity} 변경된 정보
     */
    ReviewEntity updateReviewLikes(Long reviewId, int givenLikes);

    // DELETE

    /**
     * 특정 후기 포스팅을 삭제
     *
     * @param reviewId 삭제할 포스팅 ID
     */
    void deleteReview(Long reviewId);
}
