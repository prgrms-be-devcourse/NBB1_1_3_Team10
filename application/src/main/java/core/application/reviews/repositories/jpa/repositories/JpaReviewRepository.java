package core.application.reviews.repositories.jpa.repositories;

import core.application.reviews.models.entities.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface JpaReviewRepository extends JpaRepository<ReviewEntity, Long> {

    @Query(" SELECT "
            + " (r.reviewId, r.title, r.userId, r.movieId, r.like, r.createdAt, r.updatedAt) "
            + " FROM ReviewEntity r WHERE r.reviewId = :id")
    Optional<ReviewEntity> findByReviewIdWithoutContent(Long id);

    @Query(value = " SELECT * FROM review_table "
            + " WHERE movie_id = :movieId"
            + " LIMIT :num OFFSET :offset", nativeQuery = true)
    List<ReviewEntity> findByMovieId(String movieId, int offset, int num);

    @Query(value = " SELECT * FROM review_table "
            + " WHERE movie_id = :movieId "
            + " ORDER BY created_at DESC, review_id DESC "
            + " LIMIT :num OFFSET :offset", nativeQuery = true)
    List<ReviewEntity> findByMovieIdOnDateDescend(String movieId,
            int offset, int num);

    @Query(value = " SELECT * FROM review_table "
            + " WHERE movie_id = :movieId"
            + " ORDER BY `like` DESC, review_id DESC "
            + " LIMIT :num OFFSET :offset", nativeQuery = true)
    List<ReviewEntity> findByMovieIdOnLikeDescend(String movieId,
            int offset, int num);

    Long countByMovieId(String movieId);

    List<ReviewEntity> findByUserId(UUID userId);
}
