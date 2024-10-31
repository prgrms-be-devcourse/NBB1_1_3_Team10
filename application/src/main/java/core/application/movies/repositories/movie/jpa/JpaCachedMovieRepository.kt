package core.application.movies.repositories.movie.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import core.application.movies.models.entities.CachedMovieEntity;

public interface JpaCachedMovieRepository extends JpaRepository<CachedMovieEntity, String> {

    List<CachedMovieEntity> findAllOrderBy(Sort sort);

    List<CachedMovieEntity> findOrderBy(Pageable pageable);

    @Query("select m from CachedMovieEntity m order by (m.sumOfRating / m.commentCount) desc")
    List<CachedMovieEntity> findAllOrderByAvgRating();

    @Query("select m from CachedMovieEntity m order by (m.sumOfRating / m.commentCount) desc")
    List<CachedMovieEntity> findTopXOrderByAvgRating(Pageable pageable);

    @Query("select m from CachedMovieEntity m where m.genre like %:genre% order by (m.sumOfRating / m.commentCount) desc")
    Page<CachedMovieEntity> findByGenreOrderByAvgRating(String genre, Pageable pageable);
}
