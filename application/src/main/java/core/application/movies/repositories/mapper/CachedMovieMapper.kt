package core.application.movies.repositories.mapper

import core.application.movies.models.entities.CachedMovieEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.util.*

@Mapper
interface CachedMovieMapper {
    fun save(movie: CachedMovieEntity?)

    fun findByMovieId(movieId: String?): Optional<CachedMovieEntity?>?

    fun selectOnDibOrderDescend(): List<CachedMovieEntity?>?

    fun selectOnDibOrderDescendLimit(num: Int): List<CachedMovieEntity?>?

    fun selectOnAVGRatingDescend(): List<CachedMovieEntity?>?

    fun selectOnAVGRatingDescendLimit(num: Int): List<CachedMovieEntity?>?

    fun selectOnReviewCountDescend(num: Int): List<CachedMovieEntity?>?

    fun findMoviesOnRatingDescendWithGenre(offset: Int, genre: String?): List<CachedMovieEntity?>?

    fun selectGenreMovieCount(genre: String?): Int

    fun update(@Param("movieId") movieId: String?, @Param("replacement") replacement: CachedMovieEntity?)

    fun delete(movieId: String?)
}
