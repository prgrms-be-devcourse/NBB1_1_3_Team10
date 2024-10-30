package core.application.reviews.services;

import core.application.movies.exception.*;
import core.application.movies.repositories.movie.*;
import core.application.reviews.exceptions.*;
import core.application.reviews.models.entities.*;
import core.application.reviews.repositories.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final CachedMovieRepository movieRepository;

    @FunctionalInterface
    private interface Triplet<T1, T2, T3, R> {

        R apply(T1 t1, T2 t2, T3 t3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ReviewEntity> getReviewsOnMovieId(String movieId, ReviewSortOrder order,
            boolean withContent, int offset, int num) throws NoMovieException {

        this.checkWhetherMovieExist(movieId);

        Triplet<String, Integer, Integer, List<ReviewEntity>> func
                = withContent ?

                order == ReviewSortOrder.LATEST ?       // 본문 없이 & LATEST?
                        reviewRepo::findByMovieIdOnDateDescend :
                        order == ReviewSortOrder.LIKE ? // 본문 없이 & LIKE?
                                reviewRepo::findByMovieIdOnLikeDescend :
                                null :      // ReviewSortOrder LATEST 또는 LIKE 아님? 그럼 null

                order == ReviewSortOrder.LATEST ?       // 본문 포함 & LATEST?
                        reviewRepo::findByMovieIdWithoutContentOnDateDescend :
                        order == ReviewSortOrder.LIKE ? // 본문 포함 & LIKE?
                                reviewRepo::findByMovieIdWithoutContentOnLikeDescend :
                                null;       // ReviewSortOrder LATEST 또는 LIKE 아님? 그럼 null

        return func != null ? func.apply(movieId, offset, num) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getNumberOfReviewsOnMovieId(String movieId) throws NoMovieException {

        this.checkWhetherMovieExist(movieId);

        return reviewRepo.countByMovieId(movieId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewEntity createNewReview(
            String movieId, UUID userId,
            String title, String content) throws NoMovieException {

        this.checkWhetherMovieExist(movieId);

        ReviewEntity info = ReviewEntity.builder()
                .title(title)
                .content(content)
                .build();

        return reviewRepo.saveNewReview(movieId, userId, info);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewEntity getReviewInfo(Long reviewId, boolean withContent)
            throws NoReviewFoundException {

        Optional<ReviewEntity> searchResult = withContent ?
                reviewRepo.findByReviewId(reviewId) :
                reviewRepo.findByReviewIdWithoutContent(reviewId);

        return searchResult.orElseThrow(() -> new NoReviewFoundException(reviewId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ReviewEntity updateReviewInfo(Long reviewId, ReviewEntity updateReview)
            throws NoReviewFoundException {

        ReviewEntity origin = reviewRepo.findByReviewId(reviewId)
                .orElseThrow(() -> new NoReviewFoundException(reviewId));

        String movieId = updateReview.getMovieId();
        UUID userId = updateReview.getUserId();
        boolean warningFlag = false;

        if (movieId != null && !movieId.equals(origin.getMovieId())) {
            log.warn("While updating review info, given movie ID different with origin.");
            warningFlag = true;
        }
        if (userId != null && !userId.equals(origin.getUserId())) {
            log.warn("While updating review info, given user ID different with origin.");
            warningFlag = true;
        }

        if (warningFlag) {
            log.warn("Origin Review Info : {}", origin);
            log.warn("Given Review Info : {}", updateReview);
            log.warn("Those conflict will be ignored silently.");
        }

        return reviewRepo.editReviewInfo(reviewId, updateReview);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ReviewEntity deleteReview(Long reviewId) throws NoReviewFoundException {

        ReviewEntity origin = reviewRepo.findByReviewIdWithoutContent(reviewId)
                .orElseThrow(() -> new NoReviewFoundException(reviewId));

        reviewRepo.deleteReview(reviewId);

        return origin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewEntity increaseLikes(Long reviewId) throws NoReviewFoundException {
        return updateLikes(reviewId, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewEntity decreaseLikes(Long reviewId) throws NoReviewFoundException {
        return updateLikes(reviewId, -1);
    }

    private ReviewEntity updateLikes(Long reviewId, int dl) throws NoReviewFoundException {
        ReviewEntity origin = reviewRepo.findByReviewIdWithoutContent(reviewId)
                .orElseThrow(() -> new NoReviewFoundException(reviewId));

        return reviewRepo.updateReviewLikes(reviewId, origin.getLike() + dl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkWhetherMovieExist(String movieId) throws NoMovieException {
        movieRepository.findByMovieId(movieId)
                .orElseThrow(() -> new NoMovieException("No movie found with id: ["
                        + movieId + "]"));
    }
}
