package core.application.reviews.repositories.jpa;

import core.application.reviews.exceptions.*;
import core.application.reviews.models.entities.*;
import core.application.reviews.repositories.*;
import core.application.reviews.repositories.jpa.repositories.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

@Slf4j
@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class ReviewCommentRepositoryJpaImpl implements ReviewCommentRepository {

    private final JpaReviewCommentRepository jpaRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCommentEntity saveNewReviewComment(ReviewCommentEntity reviewComment) {
        return jpaRepo.save(reviewComment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCommentEntity saveNewParentReviewComment(Long reviewId, UUID userId,
            ReviewCommentEntity reviewComment) {
        ReviewCommentEntity data = ReviewCommentEntity.builder()
                .reviewId(reviewId)
                .userId(userId)
                .content(reviewComment.getContent())
                .commentRef(reviewComment.getCommentRef())
                .isUpdated(false)
                .build();

        return jpaRepo.save(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCommentEntity saveNewChildReviewComment(Long groupId, UUID userId,
            ReviewCommentEntity reviewComment) {

        ReviewCommentEntity parent = jpaRepo.findById(groupId).orElseThrow(
                () -> new NoReviewCommentFoundException(groupId)
        );

        ReviewCommentEntity data = ReviewCommentEntity.builder()
                .reviewId(parent.getReviewId())
                .userId(userId)
                .content(reviewComment.getContent())
                .groupId(groupId)
                .commentRef(reviewComment.getCommentRef())
                .isUpdated(false)
                .build();

        return jpaRepo.save(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ReviewCommentEntity> findByReviewCommentId(Long reviewCommentId) {
        return jpaRepo.findById(reviewCommentId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> findParentCommentByReviewId(Long reviewId,
            int offset, int num) {
        return jpaRepo.findParentCommentByReviewId(reviewId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> findParentCommentByReviewIdOnDateDescend(Long reviewId,
            int offset, int num) {
        return jpaRepo.findParentCommentByReviewId(reviewId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> findParentCommentByReviewIdOnLikeDescend(Long reviewId,
            int offset, int num) {
        return jpaRepo.findParentCommentByReviewIdOnLikeDescend(reviewId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countParentCommentByReviewId(Long reviewId) {
        return jpaRepo.countParentCommentByReviewId(reviewId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> findChildCommentsByGroupId(Long groupId, int offset, int num) {
        return jpaRepo.findChildCommentsByGroupId(groupId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long countChildCommentByGroupId(Long groupId) {
        return jpaRepo.countChildCommentByGroupId(groupId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> selectAllParentComments() {
        return jpaRepo.selectAllParentComments();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewCommentEntity> selectAll() {
        return jpaRepo.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

        ReviewCommentEntity origin = jpaRepo.findById(reviewCommentId).orElseThrow(
                () -> new NoReviewCommentFoundException(reviewCommentId)
        );

        origin.changeContent(replacement.getContent());
        origin.changeCommentRef(replacement.getCommentRef());
        origin.setUpdated(update);

        return jpaRepo.save(origin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCommentEntity updateReviewCommentLikes(Long reviewCommentId, int likes) {

        ReviewCommentEntity origin = jpaRepo.findById(reviewCommentId).orElseThrow(
                () -> new NoReviewCommentFoundException(reviewCommentId)
        );

        origin.changeLikes(likes);

        return jpaRepo.save(origin);
    }
}
