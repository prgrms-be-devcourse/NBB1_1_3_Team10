package core.application.reviews.repositories.jpa.repositories

import core.application.reviews.models.entities.ReviewEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface JpaReviewRepository : JpaRepository<ReviewEntity?, Long?> {
    @Query(
        (" SELECT "
                + " new ReviewEntity (r.reviewId, r.title, null, r.userId, r.movieId, r.like, r.createdAt, r.updatedAt) "
                + " FROM ReviewEntity r WHERE r.reviewId = :id")
    )
    fun findByReviewIdWithoutContent(id: Long?): Optional<ReviewEntity?>

    @Query(
        value = (" SELECT * FROM review_table "
                + " WHERE movie_id = :movieId"
                + " LIMIT :num OFFSET :offset"), nativeQuery = true
    )
    fun findByMovieId(movieId: String?, offset: Int, num: Int): List<ReviewEntity>

    @Query(
        value = (" SELECT * FROM review_table "
                + " WHERE movie_id = :movieId "
                + " ORDER BY created_at DESC, review_id DESC "
                + " LIMIT :num OFFSET :offset"), nativeQuery = true
    )
    fun findByMovieIdOnDateDescend(
        movieId: String?,
        offset: Int, num: Int
    ): List<ReviewEntity>

    @Query(
        value = (" SELECT * FROM review_table "
                + " WHERE movie_id = :movieId"
                + " ORDER BY `like` DESC, review_id DESC "
                + " LIMIT :num OFFSET :offset"), nativeQuery = true
    )
    fun findByMovieIdOnLikeDescend(
        movieId: String?,
        offset: Int, num: Int
    ): List<ReviewEntity>

    fun countByMovieId(movieId: String?): Long

    fun findByUserId(userId: UUID?): List<ReviewEntity>
}
