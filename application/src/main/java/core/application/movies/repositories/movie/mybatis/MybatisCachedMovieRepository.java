package core.application.movies.repositories.movie.mybatis;

import core.application.movies.repositories.movie.CachedMovieRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;

import core.application.movies.models.entities.CachedMovieEntity;
import core.application.movies.repositories.mapper.CachedMovieMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Profile("mybatis")
public class MybatisCachedMovieRepository implements CachedMovieRepository {

	private final CachedMovieMapper mapper;

	@Override
	public CachedMovieEntity saveNewMovie(CachedMovieEntity movie) {
		mapper.save(movie);
		return movie;
	}

	@Override
	public Optional<CachedMovieEntity> findByMovieId(String movieId) {
		return mapper.findByMovieId(movieId);
	}

	@Override
	public List<CachedMovieEntity> selectOnDibOrderDescend() {
		return mapper.selectOnDibOrderDescend();
	}

	@Override
	public List<CachedMovieEntity> selectOnDibOrderDescend(int num) {
		return mapper.selectOnDibOrderDescendLimit(num);
	}

	@Override
	public List<CachedMovieEntity> selectOnAVGRatingDescend() {
		return mapper.selectOnAVGRatingDescend();
	}

	@Override
	public List<CachedMovieEntity> selectOnAVGRatingDescend(int num) {
		return mapper.selectOnAVGRatingDescendLimit(num);
	}

	@Override
	public List<CachedMovieEntity> selectOnReviewCountDescend(int num) {
		return mapper.selectOnReviewCountDescend(num);
	}

	@Override
	public Page<CachedMovieEntity> findMoviesLikeGenreOrderByAvgRating(int page, String genre) {
		Pageable pageable = PageRequest.of(page, 10);
		int total = mapper.selectGenreMovieCount(genre);
		List<CachedMovieEntity> find = mapper.findMoviesOnRatingDescendWithGenre(page * 10,
			genre);
		return new PageImpl<>(find, pageable, total);
	}

	@Override
	public CachedMovieEntity editMovie(String movieId, CachedMovieEntity replacement) {
		mapper.update(movieId, replacement);
		return replacement;
	}

	@Override
	public void deleteMovie(String movieId) {
		mapper.delete(movieId);
	}
}
