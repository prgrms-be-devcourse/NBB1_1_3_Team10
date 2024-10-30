package core.application.movies.repositories.movie.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import core.application.movies.models.entities.CachedMovieEntity;
import core.application.movies.repositories.movie.CachedMovieRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
@Profile("jpa")
public class CachedMovieRepositoryJPAImpl implements CachedMovieRepository {

    private final JpaCachedMovieRepository jpaCachedMovieRepository;

    @Override
    public CachedMovieEntity saveNewMovie(CachedMovieEntity movie) {
        return jpaCachedMovieRepository.save(movie);
    }

    @Override
    public Optional<CachedMovieEntity> findByMovieId(String movieId) {
        return jpaCachedMovieRepository.findById(movieId);
    }

    @Override
    public List<CachedMovieEntity> selectOnDibOrderDescend() {
        return jpaCachedMovieRepository.findAllOrderBy(Sort.by(Sort.Direction.DESC, "dibCount"));
    }

    @Override
    public List<CachedMovieEntity> selectOnDibOrderDescend(int num) {
        return jpaCachedMovieRepository.findOrderBy(PageRequest.of(0, num, Sort.by(Sort.Direction.DESC, "dibCount")));
    }

    @Override
    public List<CachedMovieEntity> selectOnAVGRatingDescend() {
        return jpaCachedMovieRepository.findAllOrderByAvgRating();
    }

    @Override
    public List<CachedMovieEntity> selectOnAVGRatingDescend(int num) {
        return jpaCachedMovieRepository.findTopXOrderByAvgRating(PageRequest.of(0, num));
    }

    @Override
    public List<CachedMovieEntity> selectOnReviewCountDescend(int num) {
        return jpaCachedMovieRepository.findOrderBy(PageRequest.of(0, num, Sort.by(Sort.Direction.DESC, "reviewCount")));
    }

    @Override
    public Page<CachedMovieEntity> findMoviesLikeGenreOrderByAvgRating(int page, String genre) {
        return jpaCachedMovieRepository.findByGenreOrderByAvgRating(genre, PageRequest.of(page, 10));
    }

    @Override
    public CachedMovieEntity editMovie(String movieId, CachedMovieEntity replacement) {
        return jpaCachedMovieRepository.save(replacement);
    }

    @Override
    public void deleteMovie(String movieId) {
        jpaCachedMovieRepository.deleteById(movieId);
    }
}
