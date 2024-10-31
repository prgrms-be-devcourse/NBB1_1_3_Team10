package core.application.movies.repositories.movie.mybatis

import core.application.movies.models.entities.CachedMovieEntity
import core.application.movies.repositories.mapper.CachedMovieMapper
import core.application.movies.repositories.movie.CachedMovieRepository
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@Repository
@Profile("mybatis")
class MybatisCachedMovieRepository @Autowired constructor(
    private val mapper: CachedMovieMapper
) : CachedMovieRepository {

    override fun saveNewMovie(movie: CachedMovieEntity?): CachedMovieEntity? {
        mapper.save(movie)
        return movie
    }

    override fun findByMovieId(movieId: String?): Optional<CachedMovieEntity?>? {
        return mapper.findByMovieId(movieId)
    }

    override fun selectOnDibOrderDescend(): List<CachedMovieEntity?>? {
        return mapper.selectOnDibOrderDescend()
    }

    override fun selectOnDibOrderDescend(num: Int): List<CachedMovieEntity?>? {
        return mapper.selectOnDibOrderDescendLimit(num)
    }

    override fun selectOnAVGRatingDescend(): List<CachedMovieEntity?>? {
        return mapper.selectOnAVGRatingDescend()
    }

    override fun selectOnAVGRatingDescend(num: Int): List<CachedMovieEntity?>? {
        return mapper.selectOnAVGRatingDescendLimit(num)
    }

    override fun selectOnReviewCountDescend(num: Int): List<CachedMovieEntity?>? {
        return mapper.selectOnReviewCountDescend(num)
    }

    override fun findMoviesLikeGenreOrderByAvgRating(page: Int?, genre: String?): Page<CachedMovieEntity?>? {
        val pageable: Pageable = PageRequest.of(page!!, 10)
        val total: Int = mapper.selectGenreMovieCount(genre)
        // find 변수를 non-nullable List로 초기화
        val find: List<CachedMovieEntity?> = mapper.findMoviesOnRatingDescendWithGenre(page * 10, genre) ?: emptyList()
        return PageImpl(find, pageable, total.toLong())
    }

    override fun editMovie(movieId: String?, replacement: CachedMovieEntity?): CachedMovieEntity? {
        mapper.update(movieId, replacement)
        return replacement
    }

    override fun deleteMovie(movieId: String?) {
        mapper.delete(movieId)
    }
}
