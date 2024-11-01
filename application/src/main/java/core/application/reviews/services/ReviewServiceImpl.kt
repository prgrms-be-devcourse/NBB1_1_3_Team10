package core.application.reviews.services

import core.application.movies.exception.NoMovieException
import core.application.movies.repositories.movie.CachedMovieRepository
import core.application.reviews.exceptions.NoReviewFoundException
import core.application.reviews.models.entities.ReviewEntity
import core.application.reviews.repositories.ReviewRepository
import core.application.reviews.services.ReviewServiceImpl.Triplet
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Slf4j
@Service
@RequiredArgsConstructor
class ReviewServiceImpl(
    private val reviewRepo: ReviewRepository,
    private val movieRepository: CachedMovieRepository
) : ReviewService {

    private fun interface Triplet<T1, T2, T3, R> {
        fun apply(t1: T1, t2: T2, t3: T3): R
    }

    /**
     * {@inheritDoc}
     */
    @Throws(NoMovieException::class)
    override fun getReviewsOnMovieId(
        movieId: String, order: ReviewSortOrder,
        withContent: Boolean, offset: Int, num: Int
    ): List<ReviewEntity> {
        this.checkWhetherMovieExist(movieId)

        val func: ((String, Int, Int) -> List<ReviewEntity>)? = when {
            withContent && order == ReviewSortOrder.LATEST -> { movieId, offset, num ->
                reviewRepo.findByMovieIdOnDateDescend(movieId, offset, num)
            }
            withContent && order == ReviewSortOrder.LIKE -> { movieId, offset, num ->
                reviewRepo.findByMovieIdOnLikeDescend(movieId, offset, num)
            }
            !withContent && order == ReviewSortOrder.LATEST -> { movieId, offset, num ->
                reviewRepo.findByMovieIdWithoutContentOnDateDescend(movieId, offset, num)
            }
            !withContent && order == ReviewSortOrder.LIKE -> { movieId, offset, num ->
                reviewRepo.findByMovieIdWithoutContentOnLikeDescend(movieId, offset, num)
            }
            else -> null
        }

        return func!!.invoke(movieId, offset, num)

    }

    /**
     * {@inheritDoc}
     */
    @Throws(NoMovieException::class)
    override fun getNumberOfReviewsOnMovieId(movieId: String): Long {
        this.checkWhetherMovieExist(movieId)

        return reviewRepo.countByMovieId(movieId)
    }

    /**
     * {@inheritDoc}
     */
    @Throws(NoMovieException::class)
    override fun createNewReview(
        movieId: String, userId: UUID,
        title: String, content: String
    ): ReviewEntity? {
        this.checkWhetherMovieExist(movieId)

        val info: ReviewEntity = ReviewEntity(
            title = title,
            content = content
        )
        return reviewRepo.saveNewReview(movieId, userId, info)
    }

    /**
     * {@inheritDoc}
     */
    @Throws(NoReviewFoundException::class)
    override fun getReviewInfo(reviewId: Long, withContent: Boolean): ReviewEntity {
        val searchResult =
            if (withContent) reviewRepo.findByReviewId(reviewId) else reviewRepo.findByReviewIdWithoutContent(
                reviewId
            )

        return searchResult.orElseThrow { NoReviewFoundException(reviewId) }!!
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Throws(NoReviewFoundException::class)
    override fun updateReviewInfo(reviewId: Long, updateReview: ReviewEntity): ReviewEntity? {
        val origin = reviewRepo.findByReviewId(reviewId)
            .orElseThrow { NoReviewFoundException(reviewId) }

        return reviewRepo.editReviewInfo(reviewId, updateReview)
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Throws(NoReviewFoundException::class)
    override fun deleteReview(reviewId: Long): ReviewEntity? {
        val origin = reviewRepo.findByReviewIdWithoutContent(reviewId)
            .orElseThrow { NoReviewFoundException(reviewId) }

        reviewRepo.deleteReview(reviewId)

        return origin
    }

    /**
     * {@inheritDoc}
     */
    @Throws(NoReviewFoundException::class)
    override fun increaseLikes(reviewId: Long): ReviewEntity {
        return updateLikes(reviewId, 1)!!
    }

    /**
     * {@inheritDoc}
     */
    @Throws(NoReviewFoundException::class)
    override fun decreaseLikes(reviewId: Long): ReviewEntity {
        return updateLikes(reviewId, -1)!!
    }

    @Throws(NoReviewFoundException::class)
    private fun updateLikes(reviewId: Long, dl: Int): ReviewEntity? {
        val origin = reviewRepo.findByReviewIdWithoutContent(reviewId)
            .orElseThrow { NoReviewFoundException(reviewId) }

        return reviewRepo.updateReviewLikes(reviewId, origin!!.like + dl)
    }

    /**
     * {@inheritDoc}
     */
    @Throws(NoMovieException::class)
    override fun checkWhetherMovieExist(movieId: String) {
        movieRepository.findByMovieId(movieId)
            .orElseThrow {
                NoMovieException(
                    ("No movie found with id: ["
                            + movieId + "]")
                )
            }
    }
}
