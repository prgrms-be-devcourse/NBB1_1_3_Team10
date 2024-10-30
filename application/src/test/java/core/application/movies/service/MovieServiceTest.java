package core.application.movies.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import core.application.movies.models.dto.response.MainPageMovieRespDTO;
import core.application.movies.models.dto.response.MainPageMoviesRespDTO;
import core.application.movies.models.dto.response.MovieDetailRespDTO;
import core.application.movies.models.entities.CachedMovieEntity;
import core.application.movies.repositories.movie.CachedMovieRepository;

@SpringBootTest
@Transactional
public class MovieServiceTest {
	@Autowired
	private MovieService movieService;
	@Autowired
	private CachedMovieRepository repository;

	@BeforeEach
	public void initTestData() {
		for (int i = 0; i < 10; i++) {
			CachedMovieEntity movieEntity = new CachedMovieEntity(
				String.valueOf(i),
				String.valueOf(i),
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
	}

	@Test
	@DisplayName("메인 페이지의 영화 목록을 보여준다.")
	public void getMainMovies() {
		// GIVEN
		MainPageMoviesRespDTO mainMovies = movieService.getMainPageMovieInfo();

		// WHEN
		List<MainPageMovieRespDTO> dib = mainMovies.getDibMovieList();
		List<MainPageMovieRespDTO> rating = mainMovies.getRatingMovieList();
		List<MainPageMovieRespDTO> review = mainMovies.getReviewMovieList();

		// THEN
		for (int i = 0; i < 10; i++) {
			MainPageMovieRespDTO movie = dib.get(i);
			assertThat(movie.getTitle()).isEqualTo(String.valueOf(9 - i));
		}
		for (int i = 0; i < 10; i++) {
			MainPageMovieRespDTO movie = rating.get(i);
			assertThat(movie.getMovieId()).isEqualTo(String.valueOf(i));
		}

		for (int i = 0; i < 10; i++) {
			MainPageMovieRespDTO movie = review.get(i);
			assertThat(movie.getMovieId()).isEqualTo(String.valueOf(9 - i));
		}
	}

	@Test
	@DisplayName("영화 상세 정보 가져오기 서비스 테스트")
	void testGetMovieDetailInfo_MovieExistsInCache() {
		// GIVEN
		String movieId = "K-36062";

		// Act
		MovieDetailRespDTO result = movieService.getMovieDetailInfo(movieId);

		// Assert
		assertNotNull(result);
		assertEquals("K-36062", result.getMovieId());
		assertEquals("댓글부대", result.getTitle());
		assertEquals("드라마,범죄", result.getGenre());
		assertEquals("20240327", result.getReleaseDate());
		assertEquals("109", result.getRunningTime());
		assertEquals("손석구, 김성철, 김동휘, 홍경, 김희원", result.getActors());
		assertEquals("안국진", result.getDirector());
	}
}
