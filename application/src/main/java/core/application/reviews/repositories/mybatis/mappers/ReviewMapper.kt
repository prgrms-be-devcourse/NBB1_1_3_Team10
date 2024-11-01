package core.application.reviews.repositories.mybatis.mappers

import core.application.reviews.models.entities.ReviewEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.util.*

@Mapper
interface ReviewMapper {
    fun saveNewReview(
        @Param("movieId") movieId: String?,
        @Param("userId") userId: UUID?,
        @Param("review") review: ReviewEntity?
    ): Int

    fun findByReviewId(reviewId: Long?): Optional<ReviewEntity?>

    fun findByReviewIdWithoutContent(reviewId: Long?): Optional<ReviewEntity?>

    fun findByMovieId(
        @Param("movieId") movieId: String?,
        @Param("offset") offset: Int,
        @Param("num") num: Int
    ): List<ReviewEntity>

    fun findByMovieIdOnDateDescend(
        @Param("movieId") movieId: String?,
        @Param("offset") offset: Int,
        @Param("num") num: Int
    ): List<ReviewEntity>

    fun findByMovieIdOnLikeDescend(
        @Param("movieId") movieId: String?,
        @Param("offset") offset: Int,
        @Param("num") num: Int
    ): List<ReviewEntity>

    fun findByMovieIdWithoutContent(
        @Param("movieId") movieId: String?,
        @Param("offset") offset: Int,
        @Param("num") num: Int
    ): List<ReviewEntity>

    fun findByMovieIdWithoutContentOnDateDescend(
        @Param("movieId") movieId: String?,
        @Param("offset") offset: Int,
        @Param("num") num: Int
    ): List<ReviewEntity>

    fun findByMovieIdWithoutContentOnLikeDescend(
        @Param("movieId") movieId: String?,
        @Param("offset") offset: Int,
        @Param("num") num: Int
    ): List<ReviewEntity>

    fun countByMovieId(movieId: String?): Long

    fun findByUserId(userId: UUID?): List<ReviewEntity>

    fun selectAll(): List<ReviewEntity>

    fun editReviewInfo(
        @Param("reviewId") reviewId: Long?,
        @Param("replacement") replacement: ReviewEntity?
    ): Int

    fun updateLikes(
        @Param("reviewId") reviewId: Long?,
        @Param("givenLikes") givenLikes: Int
    ): Int

    fun deleteReview(reviewId: Long?)
}
