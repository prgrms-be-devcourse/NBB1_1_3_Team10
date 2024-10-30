package core.application.reviews.repositories.mybatis;

import core.application.reviews.models.entities.*;
import core.application.reviews.repositories.*;
import core.application.reviews.repositories.mybatis.mappers.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

@Slf4j
@Repository
@Profile("mybatis")
@RequiredArgsConstructor
public class MyBatisReviewCommentRepository implements ReviewCommentRepository {

    private final ReviewCommentMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCommentEntity saveNewReviewComment(ReviewCommentEntity reviewComment) {
        int result = mapper.insertReviewComment(reviewComment);
        return reviewComment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCommentEntity saveNewParentReviewComment(Long reviewId, UUID userId,
            ReviewCommentEntity reviewComment) {

        // 보무 댓글은 groupId null
        ReviewCommentEntity data = ReviewCommentEntity.builder()
                .reviewId(reviewId)         // 포스팅 ID
                .userId(userId)             // 유저 ID
                .content(reviewComment.getContent())
                .groupId(null)
                .commentRef(reviewComment.getCommentRef())
                .like(0)
                .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS))   // 댓글 생성 시간
                .build();

        return this.saveNewReviewComment(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCommentEntity saveNewChildReviewComment(Long groupId, UUID userId,
            ReviewCommentEntity reviewComment) {

        ReviewCommentEntity data = ReviewCommentEntity.builder()
                .reviewId(reviewComment.getReviewId())
                .userId(userId)         // 유저 ID
                .content(reviewComment.getContent())
                .groupId(groupId)       // 부모 ID
                .commentRef(reviewComment.getCommentRef())
                .like(0)
                .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS))   // 댓글 생성 시간
                .build();

        return this.saveNewReviewComment(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ReviewCommentEntity> findByReviewCommentId(Long reviewCommentId) {
        return mapper.findByReviewCommentId(reviewCommentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> findParentCommentByReviewId(Long reviewId, int offset,
            int num) {
        return mapper.findParentCommentByReviewId(reviewId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> findParentCommentByReviewIdOnDateDescend(Long reviewId,
            int offset, int num) {
        return mapper.findParentCommentByReviewIdOnDateDescend(reviewId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> findParentCommentByReviewIdOnLikeDescend(Long reviewId,
            int offset, int num) {
        return mapper.findParentCommentByReviewIdOnLikeDescend(reviewId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countParentCommentByReviewId(Long reviewId) {
        return mapper.countParentCommentByReviewId(reviewId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> findChildCommentsByGroupId(Long groupId, int offset, int num) {
        return mapper.findChildCommentsByGroupId(groupId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countChildCommentByGroupId(Long groupId) {
        return mapper.countChildCommentByGroupId(groupId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> selectAllParentComments() {
        return mapper.selectAllParentComments();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> selectAll() {
        return mapper.selectAll();
    }

    /**
     * {@inheritDoc}
     *
     * @deprecated use instead {@link #editReviewCommentInfo(Long, ReviewCommentEntity, boolean)}
     */
    @Override
    @Deprecated
    public ReviewCommentEntity editReviewCommentInfo(Long reviewCommentId,
            ReviewCommentEntity replacement) {
        return this.editReviewCommentInfo(reviewCommentId, replacement, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCommentEntity editReviewCommentInfo(Long reviewCommentId,
            ReviewCommentEntity replacement, boolean update) {
        int result = mapper.updateReviewCommentEntity(reviewCommentId, replacement, update);
        return findByReviewCommentId(reviewCommentId).orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCommentEntity updateReviewCommentLikes(Long reviewCommentId, int likes) {
        int result = mapper.updateCommentLikes(reviewCommentId, likes);
        return findByReviewCommentId(reviewCommentId).orElseThrow();
    }
}
