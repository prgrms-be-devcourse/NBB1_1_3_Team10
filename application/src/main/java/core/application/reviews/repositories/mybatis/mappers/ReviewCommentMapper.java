package core.application.reviews.repositories.mybatis.mappers;

import core.application.reviews.models.entities.*;
import java.util.*;
import org.apache.ibatis.annotations.*;

/**
 * {@code ReviewCommentRepository} 에 사용될 {@code MyBatis mapper}
 *
 * @see core.application.reviews.repositories.mybatis.MyBatisReviewCommentRepository
 * @see core.application.reviews.repositories.ReviewCommentRepository
 */
@Mapper
public interface ReviewCommentMapper {

    /**
     * 실질적으로 DB 에 {@code insert} 하는 {@code MyBatis Query} 용 메서드
     *
     * @param data 삽입 데이터
     * @return {@code insert} 결과
     */
    int insertReviewComment(
            ReviewCommentEntity data
    );

    /**
     * 포스팅 댓글 ID 로 검색
     *
     * @param reviewCommentId 포스팅 댓글 ID
     * @return {@link Optional}{@code <}{@link ReviewCommentEntity}{@code >}
     */
    Optional<ReviewCommentEntity> findByReviewCommentId(Long reviewCommentId);

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
    List<ReviewCommentEntity> findParentCommentByReviewId(
            @Param("reviewId") Long reviewId,
            @Param("offset") int offset,
            @Param("num") int num);

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
    List<ReviewCommentEntity> findParentCommentByReviewIdOnDateDescend(
            @Param("reviewId") Long reviewId,
            @Param("offset") int offset,
            @Param("num") int num);

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
    List<ReviewCommentEntity> findParentCommentByReviewIdOnLikeDescend(
            @Param("reviewId") Long reviewId,
            @Param("offset") int offset,
            @Param("num") int num);

    /**
     * 특정 포스팅에 달린 모든 부모 댓글의 개수를 확인
     *
     * @param reviewId 검색할 포스팅 ID
     * @return 부모 댓글의 개수
     */
    long countParentCommentByReviewId(Long reviewId);

    /**
     * 특정 부모 댓글에 달린 자식 댓글들을 최신순으로 검색 (페이징)
     * <p>
     * 자식 댓글은 {@code groupId != null} 인 댓글들.
     *
     * @param groupId 부모 댓글의 ID
     * @return {@link List}{@code <}{@link ReviewCommentEntity}{@code >}
     */
    List<ReviewCommentEntity> findChildCommentsByGroupId(
            @Param("groupId") Long groupId,
            @Param("offset") int offset,
            @Param("num") int num);

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

    /**
     * 실질적으로 DB 에 {@code update} 하는 {@code MyBatis Query} 용 메서드
     *
     * @param reviewCommentId 댓글 ID
     * @param replacement     변경할 정보
     * @param update          {@code is_updated} 에 설정할 정보
     * @return {@code update} 결과
     */
    int updateReviewCommentEntity(
            @Param("reviewCommentId") Long reviewCommentId,
            @Param("replacement") ReviewCommentEntity replacement,
            @Param("isUpdated") boolean update
    );

    /**
     * 실질적으로 DB 에 리뷰 좋아요 {@code update} 하는 {@code MyBatis Query} 용 메서드
     *
     * @param reviewCommentId 댓글 ID
     * @param likes           변경될 좋아요 수
     * @return {@code update} 결과
     */
    int updateCommentLikes(@Param("reviewCommentId") Long reviewCommentId,
            @Param("likes") int likes);
}
