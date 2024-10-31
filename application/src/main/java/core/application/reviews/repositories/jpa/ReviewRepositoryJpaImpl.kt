package core.application.reviews.repositories.jpa

import core.application.reviews.exceptions.NoReviewFoundException
import core.application.reviews.models.entities.ReviewEntity
import core.application.reviews.repositories.ReviewRepository
import core.application.reviews.repositories.jpa.repositories.JpaReviewRepository
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*
import java.util.function.Consumer

@Repository
@Profile("jpa")
class ReviewRepositoryJpaImpl(
    private val jpaRepo: JpaReviewRepository
) : ReviewRepository {

    /**
     * {@inheritDoc}
     */
    override fun saveNewReview(movieId: String, userId: UUID, review: ReviewEntity): ReviewEntity {
        val data: ReviewEntity = ReviewEntity(
            movieId = movieId,
            userId = userId,
            title = review.title,
            content = review.content
        )

        return jpaRepo.save(data)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByReviewId(reviewId: Long): Optional<ReviewEntity?> {
        return jpaRepo.findById(reviewId)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByReviewIdWithoutContent(reviewId: Long): Optional<ReviewEntity?> {
        return jpaRepo.findByReviewIdWithoutContent(reviewId)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieId(movieId: String, offset: Int, num: Int): List<ReviewEntity?> {
        return jpaRepo.findByMovieId(movieId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieIdOnDateDescend(movieId: String, offset: Int, num: Int): List<ReviewEntity> {
        return jpaRepo.findByMovieIdOnDateDescend(movieId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieIdOnLikeDescend(movieId: String, offset: Int, num: Int): List<ReviewEntity> {
        return jpaRepo.findByMovieIdOnLikeDescend(movieId, offset, num)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieIdWithoutContent(movieId: String, offset: Int, num: Int): List<ReviewEntity> {
        // JPA 변경 감지 때문에 copy
        val results: List<ReviewEntity> = jpaRepo
            .findByMovieId(movieId, offset, num)
            .stream()
            .map { reviewEntity -> ReviewEntity.copyOf(reviewEntity) }
            .toList()


        results.forEach(Consumer { r: ReviewEntity -> r.changeContent(null) })

        return results
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieIdWithoutContentOnDateDescend(
        movieId: String, offset: Int,
        num: Int
    ): List<ReviewEntity> {
        // JPA 변경 감지 때문에 copy
        val results: List<ReviewEntity> = jpaRepo
            .findByMovieIdOnDateDescend(movieId, offset, num)
            .stream()
            .map { reviewEntity -> ReviewEntity.copyOf(reviewEntity) }
            .toList()

        results.forEach(Consumer { r: ReviewEntity -> r.changeContent("") })

        return results
    }

    /**
     * {@inheritDoc}
     */
    override fun findByMovieIdWithoutContentOnLikeDescend(
        movieId: String, offset: Int,
        num: Int
    ): List<ReviewEntity> {
        // JPA 변경 감지 때문에 copy
        val results: List<ReviewEntity> = jpaRepo
            .findByMovieIdOnLikeDescend(movieId, offset, num)
            .map { reviewEntity -> ReviewEntity.copyOf(reviewEntity) }
            .toList()

        results.forEach(Consumer { r: ReviewEntity -> r.changeContent("") })

        return results
    }

    /**
     * {@inheritDoc}
     */
    override fun countByMovieId(movieId: String): Long {
        return jpaRepo.countByMovieId(movieId)
    }

    /**
     * {@inheritDoc}
     */
    override fun findByUserId(userId: UUID): List<ReviewEntity> {
        return jpaRepo.findByUserId(userId)
    }

    /**
     * {@inheritDoc}
     */
    override fun selectAll(): List<ReviewEntity?> {
        return jpaRepo.findAll()
    }

    /**
     * {@inheritDoc}
     */
    override fun editReviewInfo(reviewId: Long, replacement: ReviewEntity): ReviewEntity {
        val find = jpaRepo.findById(reviewId)
        if (find.isEmpty) {
            throw NoReviewFoundException(reviewId)
        }
        val origin = find.get()

        origin.changeTitle(replacement.title)
        origin.changeContent(replacement.content)
        origin.updated()

        return jpaRepo.save(origin)
    }

    /**
     * {@inheritDoc}
     */
    override fun updateReviewLikes(reviewId: Long, givenLikes: Int): ReviewEntity {
        val find = jpaRepo.findById(reviewId)
        if (find.isEmpty) {
            throw NoReviewFoundException(reviewId)
        }
        val origin = find.get()

        origin.changeLikes(givenLikes)

        return jpaRepo.save(origin)
    }

    /**
     * {@inheritDoc}
     */
    override fun deleteReview(reviewId: Long) {
        jpaRepo.deleteById(reviewId)
    }
}
