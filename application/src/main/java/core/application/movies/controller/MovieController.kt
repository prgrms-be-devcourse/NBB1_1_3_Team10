package core.application.movies.controller;


import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import core.application.api.response.ApiResponse;
import core.application.movies.constant.Genre;
import core.application.movies.constant.MovieSearch;
import core.application.movies.exception.NotFoundUrlException;
import core.application.movies.models.dto.response.MainPageMoviesRespDTO;
import core.application.movies.models.dto.response.MovieDetailRespDTO;
import core.application.movies.models.dto.response.MovieSearchRespDTO;
import core.application.movies.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/movies")
@Tag(name = "Movie", description = "영화 관련 API")
@RequiredArgsConstructor
public class MovieController {
	private final MovieService movieService;

	@Operation(summary = "메인 페이지의 영화 목록", description = "평점 높은 순, 찜 많은 순, 리뷰 많은 순으로 제공한다.")
	@GetMapping("/list")
	public ApiResponse<MainPageMoviesRespDTO> getMainPageMovies() {
		MainPageMoviesRespDTO mainMovies = movieService.getMainPageMovieInfo();
		return ApiResponse.onSuccess(mainMovies);
	}

	@Operation(summary = "영화 상세 페이지", description = "영화의 상세 정보를 제공한다.")
	@GetMapping("/{movieId}")
	public ApiResponse<MovieDetailRespDTO> viewMovieDetails(@PathVariable("movieId") String movieId) {
		MovieDetailRespDTO movieDetail = movieService.getMovieDetailInfo(movieId);
		return ApiResponse.onSuccess(movieDetail);
	}

	@Operation(summary = "영화 통합 검색", description = "검색어를 사용해 영화를 검색한다.")
	@Parameters({
		@Parameter(name = "query", description = "검색어", example = "범죄도시"),
		@Parameter(name = "sortType", description = "정렬 타입", example = "latest"),
		@Parameter(name = "page", description = "page", example = "0")
	})
	@GetMapping("/search")
	public ApiResponse<Page<MovieSearchRespDTO>> search(@RequestParam(defaultValue = "", name = "query") String query,
		@RequestParam(defaultValue = "latest", name = "sortType") String sortType,
		@RequestParam(defaultValue = "0", name = "page") Integer page) {
		// 정렬 조건이 없다면 제작년도 순으로 제공
		Page<MovieSearchRespDTO> result;
		if (MovieSearch.isNotValid(sortType)) {
			result = movieService.searchMovies(page, MovieSearch.LATEST, query);
		} else {
			result = movieService.searchMovies(page, MovieSearch.valueOf(sortType.toUpperCase()), query);
		}
		return ApiResponse.onSuccess(result);
	}

	@Operation(summary = "영화 장르 검색", description = "영화를 장르로 구분하여 볼 수 있다.")
	@Parameters({
		@Parameter(name = "genre", description = "장르", example = "action"),
		@Parameter(name = "page", description = "페이지", example = "0"),
		@Parameter(name = "sort", description = "정렬 타입", example = "latest")
	})
	@GetMapping("/genre/{genre}")
	public ApiResponse<Page<MovieSearchRespDTO>> searchGenre(@PathVariable("genre") String genre,
		@RequestParam(defaultValue = "0", name = "page") Integer page,
		@RequestParam(defaultValue = "latest", name = "sort") String sort) {
		Page<MovieSearchRespDTO> result;
		// 잘못된 장르 검색 시, 아예 페이지 제공 X
		if (Genre.isNotValid(genre)) {
			throw new NotFoundUrlException("존재하지 않는 페이지입니다.");
		}
		// 유효하지 않은 정렬 조건이라면, 제작년도순으로 제공
		if (MovieSearch.isNotValid(sort)) {
			result = movieService.getMoviesWithGenreLatestOrder(page, Genre.valueOf(genre.toUpperCase()));
			return ApiResponse.onSuccess(result);
		}
		if (MovieSearch.isRatingOrder(sort)) {
			result = movieService.getMoviesWithGenreRatingOrder(page, Genre.valueOf(genre.toUpperCase()));
		} else {
			result = movieService.getMoviesWithGenreLatestOrder(page, Genre.valueOf(genre.toUpperCase()));
		}
		return ApiResponse.onSuccess(result);
	}
}
