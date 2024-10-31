package core.application.reviews.repositories.jpa;

import core.application.reviews.exceptions.*;
import core.application.reviews.models.entities.*;
import core.application.reviews.repositories.*;
import core.application.reviews.repositories.jpa.repositories.*;
import java.time.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

@Slf4j
@Repository
@Profile("jpa")
@RequiredArgsConstructor
public class ReviewRepositoryJpaImpl implements
        ReviewRepository {

    private final JpaReviewRepository jpaRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewEntity saveNewReview(String movieId, UUID userId, ReviewEntity review) {
        ReviewEntity data = ReviewEntity.builder()
                .movieId(movieId)
                .userId(userId)
                .title(review.getTitle())
                .content(review.getContent())
                .build();

        return jpaRepo.save(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ReviewEntity> findByReviewId(Long reviewId) {
        return jpaRepo.findById(reviewId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ReviewEntity> findByReviewIdWithoutContent(Long reviewId) {
        return jpaRepo.findByReviewIdWithoutContent(reviewId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieId(String movieId, int offset, int num) {
        return jpaRepo.findByMovieId(movieId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieIdOnDateDescend(String movieId, int offset, int num) {
        return jpaRepo.findByMovieIdOnDateDescend(movieId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieIdOnLikeDescend(String movieId, int offset, int num) {
        return jpaRepo.findByMovieIdOnLikeDescend(movieId, offset, num);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieIdWithoutContent(String movieId, int offset, int num) {
        // JPA 변경 감지 때문에 copy
        List<ReviewEntity> results = jpaRepo
                .findByMovieId(movieId, offset, num)
                .stream().map(ReviewEntity::copyOf)
                .toList();

        results.forEach(r -> r.changeContent(null));

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieIdWithoutContentOnDateDescend(String movieId, int offset,
            int num) {
        // JPA 변경 감지 때문에 copy
        List<ReviewEntity> results = jpaRepo
                .findByMovieIdOnDateDescend(movieId, offset, num)
                .stream().map(ReviewEntity::copyOf)
                .toList();

        results.forEach(r -> r.changeContent(null));

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByMovieIdWithoutContentOnLikeDescend(String movieId, int offset,
            int num) {
        // JPA 변경 감지 때문에 copy
        List<ReviewEntity> results = jpaRepo
                .findByMovieIdOnLikeDescend(movieId, offset, num)
                .stream().map(ReviewEntity::copyOf)
                .toList();

        results.forEach(r -> r.changeContent(null));

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long countByMovieId(String movieId) {

        return jpaRepo.countByMovieId(movieId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> findByUserId(UUID userId) {
        return jpaRepo.findByUserId(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> selectAll() {
        return jpaRepo.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewEntity editReviewInfo(Long reviewId, ReviewEntity replacement) {
        ReviewEntity origin = jpaRepo.findById(reviewId).orElseThrow(
                () -> new NoReviewFoundException(reviewId)
        );

        origin.changeTitle(replacement.getTitle());
        origin.changeContent(replacement.getContent());
        origin.setUpdatedAt(Instant.now());

        return jpaRepo.save(origin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewEntity updateReviewLikes(Long reviewId, int givenLikes) {
        ReviewEntity origin = jpaRepo.findById(reviewId).orElseThrow(
                () -> new NoReviewFoundException(reviewId)
        );

        origin.changeLikes(givenLikes);

        return jpaRepo.save(origin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteReview(Long reviewId) {
        jpaRepo.deleteById(reviewId);
    }
}
