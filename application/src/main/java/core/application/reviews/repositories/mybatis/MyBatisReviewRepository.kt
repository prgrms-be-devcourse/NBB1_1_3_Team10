package core.application.reviews.repositories.mybatis

import core.application.reviews.models.entities.ReviewEntity
import core.application.reviews.repositories.ReviewRepository
import core.application.reviews.repositories.mybatis.mappers.ReviewMapper
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Slf4j
@Repository
@Profile("mybatis")
@RequiredArgsConstructor
class MyBatisReviewRepository(
    private val mapper: ReviewMapper
) : ReviewRepository {

    /**
     * {@inheritDoc}
     */
    @Transactional
    override fun saveNewReview(movieId: String, userId: UUID, review: ReviewEntity): ReviewEntity {
        review.createdAt = Instant.now()
        mapper.saveNewReview(movieId, userId, review)
        return mapper.findByReviewId(review.reviewId).get()
    }

    /**
     * {@inheritDoc}
     */
    override fun findByReviewId(reviewId: Long): Optional<ReviewEntity?> {
        return mapper.findByReviewId(reviewId)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByReviewIdWithoutContent(reviewId: Long): Optional<ReviewEntity?> {
        return mapper.findByReviewIdWithoutContent(reviewId)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieId(movieId: String, offset: Int, num: Int): List<ReviewEntity> {
        return mapper.findByMovieId(movieId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieIdOnDateDescend(movieId: String, offset: Int, num: Int): List<ReviewEntity> {
        return mapper.findByMovieIdOnDateDescend(movieId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieIdOnLikeDescend(movieId: String, offset: Int, num: Int): List<ReviewEntity> {
        return mapper.findByMovieIdOnLikeDescend(movieId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieIdWithoutContent(movieId: String, offset: Int, num: Int): List<ReviewEntity> {
        return mapper.findByMovieIdWithoutContent(movieId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieIdWithoutContentOnDateDescend(
        movieId: String, offset: Int, num: Int
    ): List<ReviewEntity> {
        return mapper.findByMovieIdWithoutContentOnDateDescend(movieId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieIdWithoutContentOnLikeDescend(
        movieId: String, offset: Int, num: Int
    ): List<ReviewEntity> {
        return mapper.findByMovieIdWithoutContentOnLikeDescend(movieId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun countByMovieId(movieId: String): Long {
        return mapper.countByMovieId(movieId)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByUserId(userId: UUID): List<ReviewEntity> {
        return mapper.findByUserId(userId)
    }

    /**
     * {@inheritDoc}
     */
    override fun selectAll(): List<ReviewEntity> {
        return mapper.selectAll()
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    override fun editReviewInfo(reviewId: Long, replacement: ReviewEntity): ReviewEntity {
        replacement.updated()
        mapper.editReviewInfo(reviewId, replacement)
        return mapper.findByReviewId(reviewId).get()
    }

    @Transactional
    override fun updateReviewLikes(reviewId: Long, givenLikes: Int): ReviewEntity {
        mapper.updateLikes(reviewId, givenLikes)
        return mapper.findByReviewId(reviewId).get()
    }

    /**
     * {@inheritDoc}
     */
    override fun deleteReview(reviewId: Long) {
        mapper.deleteReview(reviewId)
    }
}
