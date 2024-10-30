package core.application.movies.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import core.application.movies.models.entities.CachedMovieEntity;
import core.application.movies.repositories.movie.CachedMovieRepository;

@SpringBootTest
@Transactional
public class MovieRepositoryTest {

	@Autowired
	CachedMovieRepository repository;

	@Test
	@DisplayName("영화를 데이터베이스에 저장한다.")
	public void save() {
		// GIVEN
		CachedMovieEntity movieEntity = new CachedMovieEntity(
			"test",
			"testTitle",
			"posterUrl",
			"액션",
			"2024-09-30",
			"줄거리",
			"122",
			"마동석, 김무열",
			"봉준호",
			1L, 2L, 3L, 4L
		);

		// WHEN
		CachedMovieEntity movie = repository.saveNewMovie(movieEntity);

		// THEN
		Optional<CachedMovieEntity> find = repository.findByMovieId(movie.getMovieId());
		checkEqualMovie(find, movie);
	}

	@Test
	@DisplayName("영화 정보를 수정한다.")
	public void update() {
		// GIVEN
		CachedMovieEntity movieEntity = new CachedMovieEntity(
			"test",
			"testTitle",
			"posterUrl",
			"액션",
			"2024-09-30",
			"줄거리",
			"122",
			"마동석, 김무열",
			"봉준호",
			1L, 2L, 3L, 4L
		);
		repository.saveNewMovie(movieEntity);

		// WHEN
		movieEntity.isCommentedWithRating(10);
		movieEntity.incrementReviewCount();
		repository.editMovie(movieEntity.getMovieId(), movieEntity);

		// THEN
		Optional<CachedMovieEntity> find = repository.findByMovieId(movieEntity.getMovieId());
		checkEqualMovie(find, movieEntity);
	}

	@Test
	@DisplayName("찜 많은 순, 평점 높은 순, 리뷰 많은 순으로 영화를 제공한다.")
	public void order() {
		// GIVEN
		for (int i = 0; i < 10; i++) {
			CachedMovieEntity movieEntity = new CachedMovieEntity(
				"test" + i,
				"testTitle",
				"posterUrl",
				"액션",
				"2024-09-30",
				"줄거리",
				"122",
				"마동석, 김무열",
				"봉준호",
				(long)i, (long)(i), 10L, (long)(100 - 10 * i)
			);
			repository.saveNewMovie(movieEntity);
		}

		// WHEN
		List<CachedMovieEntity> rating = repository.selectOnAVGRatingDescend(5);
		List<CachedMovieEntity> dib = repository.selectOnDibOrderDescend(5);
		List<CachedMovieEntity> review = repository.selectOnReviewCountDescend(5);

		// THEN
		assertThat(rating).hasSize(5);
		for (CachedMovieEntity movie : rating) {
			System.out.println(movie.getSumOfRating());
		}
		assertThat(rating.get(0).getSumOfRating()).isEqualTo(100);
		assertThat(rating.get(1).getSumOfRating()).isEqualTo(90);
		assertThat(rating.get(2).getSumOfRating()).isEqualTo(80);
		assertThat(rating.get(3).getSumOfRating()).isEqualTo(70);
		assertThat(rating.get(4).getSumOfRating()).isEqualTo(60);

		assertThat(dib).hasSize(5);
		assertThat(dib.get(0).getDibCount()).isEqualTo(9);
		assertThat(dib.get(1).getDibCount()).isEqualTo(8);
		assertThat(dib.get(2).getDibCount()).isEqualTo(7);
		assertThat(dib.get(3).getDibCount()).isEqualTo(6);
		assertThat(dib.get(4).getDibCount()).isEqualTo(5);

		assertThat(review).hasSize(5);
		assertThat(review.get(0).getReviewCount()).isEqualTo(9);
		assertThat(review.get(1).getReviewCount()).isEqualTo(8);
		assertThat(review.get(2).getReviewCount()).isEqualTo(7);
		assertThat(review.get(3).getReviewCount()).isEqualTo(6);
		assertThat(review.get(4).getReviewCount()).isEqualTo(5);
	}

	private void checkEqualMovie(Optional<CachedMovieEntity> findByRepository, CachedMovieEntity movie) {
		assertThat(findByRepository).isPresent();
		assertThat(findByRepository.get()).satisfies(find -> {
			assertThat(find.getMovieId()).isEqualTo(movie.getMovieId());
			assertThat(find.getTitle()).isEqualTo(movie.getTitle());
			assertThat(find.getPosterUrl()).isEqualTo(movie.getPosterUrl());
			assertThat(find.getGenre()).isEqualTo(movie.getGenre());
			assertThat(find.getReleaseDate()).isEqualTo(movie.getReleaseDate());
			assertThat(find.getPlot()).isEqualTo(movie.getPlot());
			assertThat(find.getRunningTime()).isEqualTo(movie.getRunningTime());
			assertThat(find.getActors()).isEqualTo(movie.getActors());
			assertThat(find.getDirector()).isEqualTo(movie.getDirector());
			assertThat(find.getDibCount()).isEqualTo(movie.getDibCount());
			assertThat(find.getReviewCount()).isEqualTo(movie.getReviewCount());
			assertThat(find.getCommentCount()).isEqualTo(movie.getCommentCount());
			assertThat(find.getSumOfRating()).isEqualTo(movie.getSumOfRating());
		});
	}

	@Test
	@DisplayName("commentCount가 0인 영화는 평균 평점 정렬 시 최하위에 정렬된다.")
	public void commentCountTest() {
		// GIVEN
		for (int i = 0; i < 8; i++) {
			CachedMovieEntity movieEntity = new CachedMovieEntity(
					"test" + i,
					"testTitle",
					"posterUrl",
					"액션",
					"2024-09-30",
					"줄거리",
					"122",
					"마동석, 김무열",
					"봉준호",
					(long)i, (long)(i), 10L, (long)(100 - 10 * i)
			);
			repository.saveNewMovie(movieEntity);
		}
		for (int i = 8; i < 10; i++) {
			CachedMovieEntity movieEntity = new CachedMovieEntity(
					"test" + i,
					"testTitle",
					"posterUrl",
					"액션",
					"2024-09-30",
					"줄거리",
					"122",
					"마동석, 김무열",
					"봉준호",
					(long)i, (long)(i), 0L, (long)(100 - 10 * i)
			);
			repository.saveNewMovie(movieEntity);
		}

	    // WHEN
		List<CachedMovieEntity> movies = repository.selectOnAVGRatingDescend();

		// THEN
		for (int i = 0; i < 8; i++) {
			System.out.println(i + " : " + movies.get(i).getMovieId());
			assertThat(movies.get(i).getSumOfRating()).isEqualTo(100 - (10 * i));
			assertThat(movies.get(i).getCommentCount()).isNotEqualTo(0);
		}
		for (int i = 8; i < 10; i++) {
			assertThat(movies.get(i).getCommentCount()).isEqualTo(0);
		}
	}

	@Test
	@DisplayName("특정 장르의 영화를 평점순으로 제공한다.")
	public void genreAvgRatingTest() {
		// GIVEN
		for (int i = 0; i < 8; i++) {
			CachedMovieEntity movieEntity = new CachedMovieEntity(
				"test" + i,
				"testTitle",
				"posterUrl",
				"액션",
				"2024-09-30",
				"줄거리",
				"122",
				"마동석, 김무열",
				"봉준호",
				(long)i, (long)(i), 10L, (long)(100 - 10 * i)
			);
			repository.saveNewMovie(movieEntity);
		}
		for (int i = 8; i < 10; i++) {
			CachedMovieEntity movieEntity = new CachedMovieEntity(
				"test" + i,
				"testTitle",
				"posterUrl",
				"스릴러",
				"2024-09-30",
				"줄거리",
				"122",
				"마동석, 김무열",
				"봉준호",
				(long)i, (long)(i), 0L, (long)(1000 - 10 * i)
			);
			repository.saveNewMovie(movieEntity);
		}

	   // WHEN
		List<CachedMovieEntity> find = repository.findMoviesLikeGenreOrderByAvgRating(0, "액션").getContent();

		// THEN
		for (int i = 0; i < find.size(); i++) {
			assertThat(find.get(i).getGenre()).isEqualTo("액션");
			assertThat(find.get(i).getSumOfRating()).isEqualTo(100 - 10 * i);
		}
	}
}
