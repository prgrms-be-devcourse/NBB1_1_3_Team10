package core.application.movies.repositories.movie.jpa

import core.application.movies.models.entities.CachedMovieEntity
import core.application.movies.repositories.movie.CachedMovieRepository
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.util.*

@RequiredArgsConstructor
@Repository
@Profile("jpa")
class CachedMovieRepositoryJPAImpl (
    private val jpaCachedMovieRepository: JpaCachedMovieRepository
): CachedMovieRepository {

    override fun saveNewMovie(movie: CachedMovieEntity?): CachedMovieEntity? {
        return movie?.let { jpaCachedMovieRepository.save(it) }
    }

    override fun findByMovieId(movieId: String?): Optional<CachedMovieEntity?>? {
        return movieId?.let { jpaCachedMovieRepository.findById(it) }
    }

    override fun selectOnDibOrderDescend(): List<CachedMovieEntity?>? {
        return jpaCachedMovieRepository.findAllOrderBy(Sort.by(Sort.Direction.DESC, "dibCount"))
    }

    override fun selectOnDibOrderDescend(num: Int): List<CachedMovieEntity?>? {
        return jpaCachedMovieRepository.findOrderBy(PageRequest.of(0, num, Sort.by(Sort.Direction.DESC, "dibCount")))
    }

    override fun selectOnAVGRatingDescend(): List<CachedMovieEntity?>? {
        return jpaCachedMovieRepository.findAllOrderByAvgRating()
    }

    override fun selectOnAVGRatingDescend(num: Int): List<CachedMovieEntity?>? {
        return jpaCachedMovieRepository.findTopXOrderByAvgRating(PageRequest.of(0, num))
    }

    override fun selectOnReviewCountDescend(num: Int): List<CachedMovieEntity?>? {
        return jpaCachedMovieRepository.findOrderBy(
            PageRequest.of(
                0,
                num,
                Sort.by(Sort.Direction.DESC, "reviewCount")
            )
        )
    }

    override fun findMoviesLikeGenreOrderByAvgRating(page: Int?, genre: String?): Page<CachedMovieEntity?>? {
        return jpaCachedMovieRepository.findByGenreOrderByAvgRating(genre, page?.let { PageRequest.of(it, 10) })
    }

    override fun editMovie(movieId: String?, replacement: CachedMovieEntity?): CachedMovieEntity? {
        return replacement?.let { jpaCachedMovieRepository.save(it) }
    }

    override fun deleteMovie(movieId: String?) {
        if (movieId != null) {
            jpaCachedMovieRepository.deleteById(movieId)
        }
    }
}
