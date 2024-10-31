package core.application.reviews.repositories.mybatis;

import core.application.reviews.models.entities.*;
import core.application.reviews.repositories.*;
import core.application.reviews.repositories.mybatis.mappers.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Repository
@Profile("mybatis")
@RequiredArgsConstructor
public class MyBatisReviewRepository implements
        ReviewRepository {

    private final ReviewMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ReviewEntity saveNewReview(String movieId, UUID userId, ReviewEntity review) {
        review.setCreatedAt(Instant.now());
        mapper.saveNewReview(movieId, userId, review);
        return mapper.findByReviewId(review.getReviewId()).orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ReviewEntity> findByReviewId(Long reviewId) {
        return mapper.findByReviewId(reviewId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ReviewEntity> findByReviewIdWithoutContent(Long reviewId) {
        return mapper.findByReviewIdWithoutContent(reviewId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieId(String movieId, int offset, int num) {
        return mapper.findByMovieId(movieId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieIdOnDateDescend(String movieId, int offset, int num) {
        return mapper.findByMovieIdOnDateDescend(movieId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieIdOnLikeDescend(String movieId, int offset, int num) {
        return mapper.findByMovieIdOnLikeDescend(movieId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieIdWithoutContent(String movieId, int offset, int num) {
        return mapper.findByMovieIdWithoutContent(movieId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieIdWithoutContentOnDateDescend(
            String movieId, int offset, int num) {
        return mapper.findByMovieIdWithoutContentOnDateDescend(movieId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieIdWithoutContentOnLikeDescend(
            String movieId, int offset, int num) {
        return mapper.findByMovieIdWithoutContentOnLikeDescend(movieId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long countByMovieId(String movieId) {
        return mapper.countByMovieId(movieId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByUserId(UUID userId) {
        return mapper.findByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> selectAll() {
        return mapper.selectAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ReviewEntity editReviewInfo(Long reviewId, ReviewEntity replacement) {
        replacement.setUpdatedAt(Instant.now());
        mapper.editReviewInfo(reviewId, replacement);
        return mapper.findByReviewId(reviewId).orElseThrow();
    }

    @Override
    @Transactional
    public ReviewEntity updateReviewLikes(Long reviewId, int givenLikes) {
        mapper.updateLikes(reviewId, givenLikes);
        return mapper.findByReviewId(reviewId).orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteReview(Long reviewId) {
        mapper.deleteReview(reviewId);
    }
}
