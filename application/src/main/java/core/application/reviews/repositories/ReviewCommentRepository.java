package core.application.reviews.repositories;


import core.application.reviews.models.entities.*;
import java.util.*;

/**
 * {@code REVIEW_COMMENT_TABLE} 과 관련된 {@code Repository}
 */
public interface ReviewCommentRepository {

    //<editor-fold desc="CREATE">

    /**
     * 새로운 포스팅 댓글을 DB 에 등록
     *
     * @param reviewComment 등록할 포스팅 댓글 정보
     * @return {@link ReviewCommentEntity} 등록된 정보
     */
    ReviewCommentEntity saveNewReviewComment(ReviewCommentEntity reviewComment);

    // 부모 댓글을 등록

    /**
     * 특정 포스팅에 주어진 유저 ID 로 부모 댓글을 등록
     *
     * @param reviewId      댓글을 등록할 영화 후기 포스팅 ID
     * @param userId        포스팅 댓글을 등록하는 유저 ID
     * @param reviewComment 등록할 포스팅 댓글 정보
     * @return {@link ReviewCommentEntity} 등록된 정보
     */
    ReviewCommentEntity saveNewParentReviewComment(Long reviewId, UUID userId,
            ReviewCommentEntity reviewComment);

    // 자식 댓글을 등록

    /**
     * 특정 포스팅 댓글에 주어진 유저 ID 로 자식 댓글을 등록
     *
     * @param groupId       부모 댓글의 포스팅 댓글 ID
     * @param userId        댓글을 등록하는 유저 ID
     * @param reviewComment 등록할 댓글 정보
     * @return {@link ReviewCommentEntity} 등록된 정보
     */
    ReviewCommentEntity saveNewChildReviewComment(Long groupId, UUID userId,
            ReviewCommentEntity reviewComment);
    //</editor-fold>

    //<editor-fold desc="READ">

    /**
     * 포스팅 댓글 ID 로 검색
     *
     * @param reviewCommentId 포스팅 댓글 ID
     * @return {@link Optional}{@code <}{@link ReviewCommentEntity}{@code >}
     */
    Optional<ReviewCommentEntity> findByReviewCommentId(Long reviewCommentId);

    //<editor-fold desc="부모 댓글 검색">

    /**
     * 특정 포스팅에 달린 모든 부모 댓글을 검색 (페이징)
     * <p>
     * 즉, {@code groupId == null} 인 댓글만 검색.
     *
     * @param reviewId 검색할 포스팅 ID
     * @param offset   댓글 offset
     * @param num      가져올 댓글 수
     * @return {@link List}{@code <}{@link ReviewCommentEntity}{@code >}
     */
    List<ReviewCommentEntity> findParentCommentByReviewId(Long reviewId, int offset, int num);

    /**
     * 특정 포스팅에 달린 모든 부모 댓글을 최신순으로 검색 (페이징)
     * <p>
     * 즉, {@code groupId == null} 인 댓글만 검색.
     *
     * @param reviewId 검색할 포스팅 ID
     * @param offset   오프셋
     * @param num      가져올 개수
     * @return {@link List}{@code <}{@link ReviewCommentEntity}{@code >}
     */
    List<ReviewCommentEntity> findParentCommentByReviewIdOnDateDescend(Long reviewId, int offset,
            int num);

    /**
     * 특정 포스팅에 달린 모든 부모 댓글을 좋아요 순으로 검색 (페이징)
     * <p>
     * 즉, {@code groupId == null} 인 댓글만 검색.
     *
     * @param reviewId 검색할 포스팅 ID
     * @param offset   오프셋
     * @param num      가져올 개수
     * @return {@link List}{@code <}{@link ReviewCommentEntity}{@code >}
     */
    List<ReviewCommentEntity> findParentCommentByReviewIdOnLikeDescend(Long reviewId, int offset,
            int num);

    /**
     * 특정 포스팅에 달린 모든 부모 댓글의 개수를 확인
     *
     * @param reviewId 검색할 포스팅 ID
     * @return 부모 댓글의 개수
     */
    long countParentCommentByReviewId(Long reviewId);

    //</editor-fold>


    /**
     * 특정 부모 댓글에 달린 자식 댓글들을 최신순으로 검색 (페이징)
     * <p>
     * 자식 댓글은 {@code groupId != null} 인 댓글들.
     *
     * @param groupId 부모 댓글의 ID
     * @return {@link List}{@code <}{@link ReviewCommentEntity}{@code >}
     */
    List<ReviewCommentEntity> findChildCommentsByGroupId(Long groupId, int offset, int num);

    /**
     * 특정 부모 댓글 아래 자식 댓글의 개수를 확인
     *
     * @param groupId 부모 댓글 ID
     * @return 자식 댓글의 개수
     */
    long countChildCommentByGroupId(Long groupId);


    /**
     * DB 에 저장된 모든 부모 포스팅 댓글을 검색
     * <p>
     * 즉, {@code groupId == null} 인 댓글만 검색.
     *
     * @return {@link List}{@code <}{@link ReviewCommentEntity}{@code >}
     */
    List<ReviewCommentEntity> selectAllParentComments();

    /**
     * DB 에 저장된 모든 포스팅 댓글을 검색
     * <p>
     * 자식 댓글은 {@code groupId != null} 인 댓글들.
     *
     * @return {@link List}{@code <}{@link ReviewCommentEntity}{@code >}
     */
    List<ReviewCommentEntity> selectAll();
    //</editor-fold>

    // UPDATE

    /**
     * 특정 포스팅 댓글의 정보를 {@code replacement} 정보로 변경
     * <p>
     * 이 때 {@code content}, {@code commentRef} 만 {@code replacement} 의 것으로 변경. {@code isUpdated} 는
     * 자동으로 변경.
     *
     * @param reviewCommentId 정보 변경할 포스팅 댓글의 ID
     * @param replacement     변경할 정보
     * @return {@link ReviewCommentEntity} 변경된 정보
     * @deprecated use instead {@link #editReviewCommentInfo(Long, ReviewCommentEntity, boolean)}
     */
    @Deprecated
    ReviewCommentEntity editReviewCommentInfo(Long reviewCommentId,
            ReviewCommentEntity replacement);

    /**
     * 특정 포스팅 댓글의 정보를 {@code replacement} 정보로 변경
     * <p>
     * 이 때 {@code content}, {@code commentRef} 만 {@code replacement} 의 것으로 변경. {@code isUpdated} 는
     * 주어진 대로 변경.
     *
     * @param reviewCommentId 정보 변경할 포스팅 댓글의 ID
     * @param replacement     변경할 정보
     * @param update          {@code is_updated} 에 설정할 정보
     * @return {@link ReviewCommentEntity} 변경된 정보
     */
    ReviewCommentEntity editReviewCommentInfo(Long reviewCommentId, ReviewCommentEntity replacement,
            boolean update);

    /**
     * 특정 포스팅 댓글의 좋아요를 수정
     *
     * @param reviewCommentId 수정할 댓글 ID
     * @param likes           설정할 좋아요 수
     * @return {@link ReviewCommentEntity} 변경된 정보
     */
    ReviewCommentEntity updateReviewCommentLikes(Long reviewCommentId, int likes);
}
