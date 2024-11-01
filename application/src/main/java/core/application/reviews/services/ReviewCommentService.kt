package core.application.reviews.services;

import java.util.List;
import java.util.UUID;

import core.application.reviews.exceptions.NoReviewCommentFoundException;
import core.application.reviews.exceptions.NoReviewFoundException;
import core.application.reviews.models.entities.ReviewCommentEntity;

/**
 * 영화 후기 포스팅 댓글과 관련된 서비스
 */
public interface ReviewCommentService {

	/**
	 * 특정 포스팅 내 부모 댓글의 개수를 조회하는 서비스
	 *
	 * @param reviewId 영화 후기 포스팅 ID
	 * @return 부모 댓글의 개수
	 */
	long getNumberOfParentComment(Long reviewId) throws NoReviewFoundException;

	/**
	 * 특정 부모 댓글 아래 자식 댓글의 개수를 조회하는 서비스
	 *
	 * @param groupId 부모 댓글 ID
	 * @return 자식 댓글의 개수
	 */
	long getNumberOfChildComment(Long groupId) throws NoReviewCommentFoundException;

	boolean doesUserOwnsComment(UUID userId, Long reviewCommentId)
		throws NoReviewCommentFoundException;

	/**
	 * 특정 리뷰 포스팅의 부모 댓글을 불러오는 서비스
	 *
	 * @param reviewId 리뷰 포스팅 ID
	 * @param order    보모 댓글 정렬 순서 {@code (최신순, 좋아요순)}
	 * @param offset   댓글 offset
	 * @param num      가져올 댓글의 개수
	 * @return {@link List}{@code <}{@link ReviewCommentEntity}{@code >}
	 * @throws NoReviewFoundException {@code reviewId} 에 해당하는 리뷰를 찾지 못했을 시
	 * @author jbw9964
	 * @see ReviewCommentSortOrder
	 */
	List<ReviewCommentEntity> getParentReviewComments(Long reviewId, ReviewCommentSortOrder order,
		int offset, int num)
		throws NoReviewFoundException;

	/**
	 * 특정 부모 댓글의 자식 댓글을 불러오는 서비스
	 * <p>
	 * 이 때 불러오는 자식 댓글은 최신순
	 *
	 * @param reviewId 댓글이 달리는 포스팅 ID
	 * @param groupId  부모 댓글의 ID
	 * @param offset   댓글 offset
	 * @param num      가져올 댓글 개수
	 * @return {@link List}{@code <}{@link ReviewCommentEntity}{@code >}
	 * @author jbw9964
	 */
	List<ReviewCommentEntity> getChildReviewCommentsOnParent(Long reviewId, Long groupId,
		int offset, int num);

	/**
	 * 특정 포스팅에 부모 댓글 다는 서비스
	 *
	 * @param reviewId            포스팅 ID
	 * @param userId              댓글 작성하는 유저 ID
	 * @param parentReviewComment 댓글 정보
	 * @return {@link ReviewCommentEntity} 등록된 댓글 정보
	 * @throws NoReviewFoundException {@code reviewId} 에 해당하는 리뷰를 찾지 못했을 시
	 * @author jbw9964
	 */
	ReviewCommentEntity addNewParentReviewComment(Long reviewId, UUID userId,
		ReviewCommentEntity parentReviewComment)
		throws NoReviewFoundException;

	/**
	 * 특정 포스팅 내 부모 댓글에 자식 댓글 다는 서비스
	 *
	 * @param reviewId           댓글이 작성되는 포스팅 ID
	 * @param groupId            부모 댓글 ID
	 * @param userId             댓글 작성하는 유저 ID
	 * @param childReviewComment 자식 댓글 정보
	 * @return {@link ReviewCommentEntity} 등록된 댓글 정보
	 * @throws NoReviewFoundException        {@code reviewId} 에 해당하는 리뷰를 찾지 못했을 시
	 * @throws NoReviewCommentFoundException {@code reviewId} 에 해당하는 부모 댓글을 찾지 못했을 시
	 * @author jbw9964
	 */
	ReviewCommentEntity addNewChildReviewComment(
		Long reviewId, Long groupId, UUID userId, ReviewCommentEntity childReviewComment
	) throws NoReviewFoundException, NoReviewCommentFoundException;

	/**
	 * 특정 댓글의 내용을 수정하는 서비스
	 *
	 * @param reviewCommentId    수정할 댓글 ID
	 * @param commentRef         {@code (필요하다면)} 멘션할 댓글 ID
	 * @param contentReplacement 수정할 댓글 내용
	 * @return {@link ReviewCommentEntity} 수정된 정보
	 * @throws NoReviewCommentFoundException {@code reviewCommentId} 에 해당하는 댓글을 찾지 못했을 시
	 * @author jbw9964
	 */
	ReviewCommentEntity editReviewComment(Long reviewCommentId, Long commentRef,
		String contentReplacement)
		throws NoReviewCommentFoundException;

	/**
	 * 특정 댓글을 삭제하는 서비스
	 * <p>
	 * 이 때 DB 에 정말로 삭제되는게 아닌 {@code content} 가 {@code (댓글이 삭제되었습니다.)} 로 변경됨.
	 *
	 * @param reviewCommentId 삭제할 댓글 ID
	 * @return {@link ReviewCommentEntity} 삭제된 된 댓글 정보
	 * @throws NoReviewCommentFoundException {@code reviewCommentId} 에 해당하는 댓글을 찾지 못했을 시
	 * @author jbw9964
	 */
	ReviewCommentEntity deleteReviewComment(Long reviewCommentId)
		throws NoReviewCommentFoundException;

	/**
	 * 특정 댓글의 좋아요를 1 증가시키는 서비스
	 *
	 * @param reviewCommentId 좋아요 누를 댓글 ID
	 * @return {@link ReviewCommentEntity} 좋아요가 1 증가한 댓글 정보
	 * @throws NoReviewCommentFoundException {@code reviewCommentId} 에 해당하는 댓글을 찾지 못했을 시
	 * @author jbw9964
	 */
	ReviewCommentEntity increaseCommentLike(Long reviewCommentId)
		throws NoReviewCommentFoundException;

	/**
	 * 특정 댓글의 좋아요를 1 감소시키는 서비스
	 *
	 * @param reviewCommentId 좋아요 취소할 댓글 ID
	 * @return {@link ReviewCommentEntity} 좋아요가 1 감소된 댓글 정보
	 * @throws NoReviewCommentFoundException {@code reviewCommentId} 에 해당하는 댓글을 찾지 못했을 시
	 * @author jbw9964
	 */
	ReviewCommentEntity decreaseCommentLike(Long reviewCommentId)
		throws NoReviewCommentFoundException;
}
