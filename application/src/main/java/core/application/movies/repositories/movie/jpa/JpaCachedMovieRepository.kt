package core.application.movies.repositories.movie.jpa

import core.application.movies.models.entities.CachedMovieEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface JpaCachedMovieRepository : JpaRepository<CachedMovieEntity?, String?> {
    fun findAllOrderBy(sort: Sort?): List<CachedMovieEntity?>?

    fun findOrderBy(pageable: Pageable?): List<CachedMovieEntity?>?

    @Query("select m from CachedMovieEntity m order by (m.sumOfRating / m.commentCount) desc")
    fun findAllOrderByAvgRating(): List<CachedMovieEntity?>?

    @Query("select m from CachedMovieEntity m order by (m.sumOfRating / m.commentCount) desc")
    fun findTopXOrderByAvgRating(pageable: Pageable?): List<CachedMovieEntity?>?

    @Query("select m from CachedMovieEntity m where m.genre like %:genre% order by (m.sumOfRating / m.commentCount) desc")
    fun findByGenreOrderByAvgRating(genre: String?, pageable: Pageable?): Page<CachedMovieEntity?>?
}
