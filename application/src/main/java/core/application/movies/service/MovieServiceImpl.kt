package core.application.movies.service;

import core.application.movies.repositories.movie.mybatis.MybatisCachedMovieRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import core.application.movies.constant.Genre;
import core.application.movies.constant.KmdbParameter;
import core.application.movies.constant.MovieSearch;
import core.application.movies.exception.NoMovieException;
import core.application.movies.exception.NoSearchResultException;
import core.application.movies.models.dto.response.MainPageMovieRespDTO;
import core.application.movies.models.dto.response.MainPageMoviesRespDTO;
import core.application.movies.models.dto.response.MovieDetailRespDTO;
import core.application.movies.models.dto.response.MovieSearchRespDTO;
import core.application.movies.models.entities.CachedMovieEntity;
import core.application.movies.repositories.movie.CachedMovieRepository;
import core.application.movies.repositories.movie.KmdbApiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

	@Value("${kmdb.api.key}")
	private String apiKey;
	@Value("${kmdb.api.default.image}")
	private String defaultImgUrl;
	private final String DEFAULT_MESSAGE = "알 수 없음";
	private final CachedMovieRepository movieRepository;
	private final KmdbApiRepository kmdbRepository;

	@Override
	@Transactional(readOnly = true)
	public MainPageMoviesRespDTO getMainPageMovieInfo() {
		List<MainPageMovieRespDTO> ratingOrder = movieRepository.selectOnAVGRatingDescend(10).stream()
			.map(MainPageMovieRespDTO::from)
			.toList();

		List<MainPageMovieRespDTO> dibOrder = movieRepository.selectOnDibOrderDescend(10).stream()
			.map(MainPageMovieRespDTO::from)
			.toList();

		List<MainPageMovieRespDTO> reviewOrder = movieRepository.selectOnReviewCountDescend(10).stream()
			.map(MainPageMovieRespDTO::from)
			.toList();

		return MainPageMoviesRespDTO.of(dibOrder, ratingOrder, reviewOrder);
	}

	@Override
	public Page<MovieSearchRespDTO> searchMovies(Integer page, MovieSearch sort, String query) {
		Map<KmdbParameter, String> params = new HashMap<>();
		params.put(KmdbParameter.START_COUNT, String.valueOf(page * 10));
		params.put(KmdbParameter.SORT, sort.SORT);
		params.put(KmdbParameter.QUERY, query);

		JSONObject jsonResponse = kmdbRepository.getResponse(params);
		List<MovieSearchRespDTO> searchResult = new ArrayList<>();
		try {
			Pageable pageable = PageRequest.of(page, 10);
			int totalMovie = jsonResponse.optInt("TotalCount");
			parseMoviesFromMovieArray(
				parseMovieArrayFromJsonResponse(jsonResponse),
				searchResult);
			return new PageImpl<>(searchResult, pageable, totalMovie);
		} catch (JSONException e) {
			log.info("[MovieService.searchMovies] '{}'에 해당하는 검색 결과가 존재하지 않음.", query);
			throw new NoSearchResultException("'" + query + "'에 해당하는 영화가 없습니다.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<MovieSearchRespDTO> getMoviesWithGenreRatingOrder(Integer page, Genre genre) {
		log.info("[MovieService.getMoviesWithGenreRatingOrder] {} 영화 평점순 제공", genre.PARAMETER);
		return movieRepository.findMoviesLikeGenreOrderByAvgRating(page, genre.PARAMETER)
			.map(MovieSearchRespDTO::from);
	}

	public Page<MovieSearchRespDTO> getMoviesWithGenreLatestOrder(Integer page, Genre genre) {
		log.info("[MovieService.getMoviesWithGenreRatingOrder] {} 영화 최신순 제공", genre.PARAMETER);
		List<MovieSearchRespDTO> result = new ArrayList<>();

		Map<KmdbParameter, String> params = new HashMap<>();
		params.put(KmdbParameter.START_COUNT, String.valueOf(page * 10));
		params.put(KmdbParameter.SORT, MovieSearch.LATEST.SORT);
		params.put(KmdbParameter.GENRE, genre.PARAMETER);

		JSONObject jsonResponse = kmdbRepository.getResponse(params);
		try {
			Pageable pageable = PageRequest.of(page, 10);
			int totalMovie = jsonResponse.optInt("TotalCount");
			parseMoviesFromMovieArray(
				parseMovieArrayFromJsonResponse(jsonResponse),
				result);
			return new PageImpl<>(result, pageable, totalMovie);
		} catch (JSONException e) {
			log.info("[MovieService.getMoviesWithGenreLatestOrder] {} 장르 영화 검색 결과가 존재하지 않음", genre.PARAMETER);
			throw new NoSearchResultException(genre.PARAMETER + " 장르에 더 이상 제공되는 영화가 없습니다.");
		}
	}

	@Transactional
	public MovieDetailRespDTO getMovieDetailInfo(String movieId) {
		Optional<CachedMovieEntity> find = movieRepository.findByMovieId(movieId);
		if (find.isPresent()) {
			log.info("[MovieService.getMovieDetailInfo] {} 영화 존재하므로 DB 내에서 제공", movieId);
			return MovieDetailRespDTO.Companion.from(find.get());
		}

		String[] docId = movieId.split("-");
		if (docId.length != 2) {
			throw new NoMovieException("해당하는 영화가 존재하지 않습니다.");
		}
		String kmdbId = docId[0];
		String kmdbSeq = docId[1];

		Map<KmdbParameter, String> params = new HashMap<>();
		params.put(KmdbParameter.MOVIE_ID, kmdbId);
		params.put(KmdbParameter.MOVIE_SEQ, kmdbSeq);

		log.info("[MovieService.getMovieDetailInfo] {} 영화 존재하지 않으므로 KMDB를 통해 조회 후 DB에 저장 시도", movieId);
		JSONObject jsonResponse = kmdbRepository.getResponse(params);
		try {
			JSONArray movieArray = parseMovieArrayFromJsonResponse(jsonResponse);
			CachedMovieEntity movieEntity = parseCachedMovieFromJsonMovie(movieArray);
			movieRepository.saveNewMovie(movieEntity);
			log.info("[MovieService.getMovieDetailInfo] {} 영화 저장 완료", movieEntity.getMovieId());
			return MovieDetailRespDTO.Companion.from(movieEntity);
		} catch (JSONException e) {
			log.info("[MovieService.getMovieDetailInfo] KMDB API를 통해 영화 조회결과가 적절하지 않음.");
			throw new NoMovieException("해당 영화는 제공되지 않습니다.");
		}
	}

	private CachedMovieEntity parseCachedMovieFromJsonMovie(JSONArray movieArray) {
		JSONObject jsonMovie = movieArray.getJSONObject(0);

		String movieId = getDataWithException(jsonMovie.optString("movieId") + "-" + jsonMovie.optString("movieSeq"));
		String title = getDataWithException(
			jsonMovie.optString("title").replaceAll("!HS", "").replaceAll("!HE", "").trim());
		String imgUrl = getDataWithDefault(jsonMovie.optString("posters"), defaultImgUrl).split("\\|")[0];
		String genre = getDataWithDefault(jsonMovie.optString("genre"), DEFAULT_MESSAGE);
		String ReleaseDate = getDataWithDefault(jsonMovie.optString("repRlsDate", ""), DEFAULT_MESSAGE);
		String plot = safeParsePlotWithDefault(jsonMovie);
		String runtime = getDataWithDefault(jsonMovie.optString("runtime"), DEFAULT_MESSAGE);
		String actors = safeParseActorsWithDefault(jsonMovie);
		String director = safeParseDirectorWithDefault(jsonMovie);
		return new CachedMovieEntity(movieId, title, imgUrl, genre, ReleaseDate,
			plot, runtime, actors, director, 0L, 0L, 0L, 0L);
	}

	private String safeParsePlotWithDefault(JSONObject jsonMovie) {
		try {
			JSONArray plotArray = jsonMovie.optJSONObject("plots").optJSONArray("plot");
			for (int j = 0; j < plotArray.length(); j++) {
				JSONObject plot = plotArray.getJSONObject(j);
				if (plot.optString("plotLang").equals("한국어")) {
					return getDataWithDefault(plot.optString("plotText"), DEFAULT_MESSAGE);
				}
			}
		} catch (JSONException e) {
			return DEFAULT_MESSAGE;
		}
		return DEFAULT_MESSAGE;
	}

	private String safeParseActorsWithDefault(JSONObject jsonMovie) {
		StringBuilder actorName = new StringBuilder();
		try {
			JSONArray actorsArray = jsonMovie.optJSONObject("actors").optJSONArray("actor");
			for (int k = 0; k < Math.min(actorsArray.length(), 5); k++) {
				actorName.append(", ")
					.append(getDataWithDefault(actorsArray.getJSONObject(k).optString("actorNm"), DEFAULT_MESSAGE));
				if (actorName.toString().equals(", 알 수 없음"))
					break;
			}
			actorName = new StringBuilder(actorName.substring(2));
		} catch (JSONException e) {
			actorName = new StringBuilder(DEFAULT_MESSAGE);
		}
		return actorName.toString();
	}

	private String safeParseDirectorWithDefault(JSONObject jsonMovie) {
		String resultDirector;
		try {
			JSONArray directorsArray = jsonMovie.optJSONObject("directors").optJSONArray("director");
			resultDirector = getDataWithDefault(directorsArray.getJSONObject(0).optString("directorNm"),
				DEFAULT_MESSAGE);
		} catch (JSONException e) {
			resultDirector = DEFAULT_MESSAGE;
		}
		return resultDirector;
	}

	private String getDataWithException(String str) {
		return Optional.ofNullable(str)
			.filter(val -> !val.isEmpty())  // 빈 문자열이 아닐 때만 처리
			.orElseThrow(() -> new NoMovieException("제공하지 않는 영화입니다."));
	}

	private String getDataWithDefault(String input, String defaultString) {
		return input == null || input.trim().isEmpty() ? defaultString : input;
	}

	private JSONArray parseMovieArrayFromJsonResponse(JSONObject jsonResponse) {
		return jsonResponse.getJSONArray("Data")
			.getJSONObject(0)
			.getJSONArray("Result");
	}

	private void parseMoviesFromMovieArray(JSONArray movieArray, List<MovieSearchRespDTO> result) {
		for (int i = 0; i < movieArray.length(); i++) {
			JSONObject movie = movieArray.getJSONObject(i);
			String id = getDataWithException(movie.optString("movieId") + "-" + movie.optString("movieSeq"));
			String title = getDataWithException(
				movie.optString("title")
					.replaceAll("!HS", "").
					replaceAll("!HE", "").
					trim());
			String imgUrl = getDataWithDefault(movie.optString("posters"), defaultImgUrl).split("\\|")[0];
			String producedYear = getDataWithDefault(movie.optString("prodYear"), DEFAULT_MESSAGE);
			MovieSearchRespDTO search = new MovieSearchRespDTO(id, title, imgUrl, producedYear);
			result.add(search);
		}
	}
}
