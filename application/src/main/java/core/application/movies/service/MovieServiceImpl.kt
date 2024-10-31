package core.application.movies.service

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import core.application.movies.constant.Genre
import core.application.movies.constant.KmdbParameter
import core.application.movies.constant.MovieSearch
import core.application.movies.exception.NoMovieException
import core.application.movies.exception.NoSearchResultException
import core.application.movies.models.dto.response.MainPageMovieRespDTO
import core.application.movies.models.dto.response.MainPageMoviesRespDTO
import core.application.movies.models.dto.response.MovieDetailRespDTO
import core.application.movies.models.dto.response.MovieSearchRespDTO
import core.application.movies.models.entities.CachedMovieEntity
import core.application.movies.repositories.movie.CachedMovieRepository
import core.application.movies.repositories.movie.KmdbApiRepository
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode.log

@Service
@Slf4j
@RequiredArgsConstructor
class MovieServiceImpl(
    private val movieRepository: CachedMovieRepository,
    private val kmdbRepository: KmdbApiRepository
) : MovieService {

    @Value("\${kmdb.api.key}")
    lateinit var apiKey: String

    @Value("\${kmdb.api.default.image}")
    lateinit var defaultImgUrl: String

    private val DEFAULT_MESSAGE = "알 수 없음"

    @Transactional(readOnly = true)
    override fun getMainPageMovieInfo(): MainPageMoviesRespDTO {
        val ratingOrder = movieRepository.selectOnAVGRatingDescend(10)
            ?.map { it?.let { it1 -> MainPageMovieRespDTO.from(it1) } }
        val dibOrder = movieRepository.selectOnDibOrderDescend(10)
            ?.map { it?.let { it1 -> MainPageMovieRespDTO.from(it1) } }
        val reviewOrder = movieRepository.selectOnReviewCountDescend(10)
            ?.map { it?.let { it1 -> MainPageMovieRespDTO.from(it1) } }

        return MainPageMoviesRespDTO.of(dibOrder, ratingOrder, reviewOrder)
    }

    override fun searchMovies(page: Int?, sort: MovieSearch?, query: String?): Page<MovieSearchRespDTO?>? {
        val params = mutableMapOf(
            KmdbParameter.START_COUNT to (page?.times(10) ?: 0).toString(), // page가 null일 경우 0으로 설정
            KmdbParameter.SORT to (sort?.SORT ?: MovieSearch.RANK.SORT), // sort가 null일 경우 기본값 RANK 사용
            KmdbParameter.QUERY to query
        )


        val jsonResponse = kmdbRepository.getResponse(params)
        val searchResult = mutableListOf<MovieSearchRespDTO?>()
        return try {
            val pageable: Pageable = PageRequest.of(page ?: 0, 10) // page가 null일 경우 0으로 설정

            val totalMovie = jsonResponse.optInt("TotalCount")
            parseMoviesFromMovieArray(parseMovieArrayFromJsonResponse(jsonResponse), searchResult)
            PageImpl(searchResult, pageable, totalMovie.toLong())
        } catch (e: JSONException) {
            log.info("[MovieService.searchMovies] '${query}'에 해당하는 검색 결과가 존재하지 않음.")
            throw NoSearchResultException("'$query'에 해당하는 영화가 없습니다.")
        }
    }

    @Transactional(readOnly = true)
    override fun getMoviesWithGenreRatingOrder(page: Int?, genre: Genre?): Page<MovieSearchRespDTO?>? {
        if (genre != null) {
            log.info("[MovieService.getMoviesWithGenreRatingOrder] '${genre.PARAMETER}' 영화 평점순 제공")
        }
        if (genre != null) {
            return movieRepository.findMoviesLikeGenreOrderByAvgRating(page, genre.PARAMETER)
                ?.map { it?.let { it1 -> MovieSearchRespDTO.from(it1) } }
        }
        return null;
    }

    override fun getMoviesWithGenreLatestOrder(page: Int?, genre: Genre?): Page<MovieSearchRespDTO?>? {
        log.info("[MovieService.getMoviesWithGenreLatestOrder] '${genre!!.PARAMETER}' 영화 최신순 제공")
        val result = mutableListOf<MovieSearchRespDTO?>()

        val params: MutableMap<KmdbParameter, String?> = mutableMapOf(
            KmdbParameter.START_COUNT to (page?.times(10)).toString(),
            KmdbParameter.SORT to MovieSearch.LATEST.SORT,
            KmdbParameter.GENRE to genre.PARAMETER
        )

        val jsonResponse = kmdbRepository.getResponse(params)
        return try {
            val pageable: Pageable = PageRequest.of(page!!, 10)
            val totalMovie = jsonResponse.optInt("TotalCount")
            parseMoviesFromMovieArray(parseMovieArrayFromJsonResponse(jsonResponse), result)
            PageImpl(result, pageable, totalMovie.toLong())
        } catch (e: JSONException) {
            log.info("[MovieService.getMoviesWithGenreLatestOrder] '${genre.PARAMETER}' 장르 영화 검색 결과가 존재하지 않음")
            throw NoSearchResultException("${genre.PARAMETER} 장르에 더 이상 제공되는 영화가 없습니다.")
        }
    }

    @Transactional
    override fun getMovieDetailInfo(movieId: String?): MovieDetailRespDTO? {
        val find = movieRepository.findByMovieId(movieId)
        if (find!!.isPresent) {
            log.info("[MovieService.getMovieDetailInfo] '${movieId}' 영화 존재하므로 DB 내에서 제공")
            return MovieDetailRespDTO.from(find.get())
        }

        val docId = movieId?.split("-")
        if (docId!!.size != 2) throw NoMovieException("해당하는 영화가 존재하지 않습니다.")

        val (kmdbId, kmdbSeq) = docId
        val params: MutableMap<KmdbParameter, String?> = mutableMapOf(
            KmdbParameter.MOVIE_ID to kmdbId,
            KmdbParameter.MOVIE_SEQ to kmdbSeq
        )


        log.info("[MovieService.getMovieDetailInfo] '${movieId}' 영화 존재하지 않으므로 KMDB를 통해 조회 후 DB에 저장 시도")
        val jsonResponse = kmdbRepository.getResponse(params)
        return try {
            val movieArray = parseMovieArrayFromJsonResponse(jsonResponse)
            val movieEntity = parseCachedMovieFromJsonMovie(movieArray)
            movieRepository.saveNewMovie(movieEntity)
            log.info("[MovieService.getMovieDetailInfo] '${movieEntity.movieId}' 영화 저장 완료")
            MovieDetailRespDTO.from(movieEntity)
        } catch (e: JSONException) {
            log.info("[MovieService.getMovieDetailInfo] KMDB API를 통해 영화 조회결과가 적절하지 않음.")
            throw NoMovieException("해당 영화는 제공되지 않습니다.")
        }
    }

    private fun parseCachedMovieFromJsonMovie(movieArray: JSONArray): CachedMovieEntity {
        val jsonMovie = movieArray.getJSONObject(0)

        val movieId = getDataWithException("${jsonMovie.optString("movieId")}-${jsonMovie.optString("movieSeq")}")
        val title = getDataWithException(
            jsonMovie.optString("title")
                .replace("!HS", "")
                .replace("!HE", "")
                .trim()
        )
        val imgUrl = getDataWithDefault(jsonMovie.optString("posters"), defaultImgUrl).split("|")[0]
        val genre = getDataWithDefault(jsonMovie.optString("genre"), DEFAULT_MESSAGE)
        val releaseDate = getDataWithDefault(jsonMovie.optString("repRlsDate"), DEFAULT_MESSAGE)
        val plot = safeParsePlotWithDefault(jsonMovie)
        val runtime = getDataWithDefault(jsonMovie.optString("runtime"), DEFAULT_MESSAGE)
        val actors = safeParseActorsWithDefault(jsonMovie)
        val director = safeParseDirectorWithDefault(jsonMovie)

        return CachedMovieEntity(
            movieId,
            title,
            imgUrl,
            genre,
            releaseDate,
            plot,
            runtime,
            actors,
            director,
            0L,
            0L,
            0L,
            0L
        )
    }

    private fun safeParsePlotWithDefault(jsonMovie: JSONObject): String {
        return try {
            val plotArray = jsonMovie.optJSONObject("plots")?.optJSONArray("plot") ?: return DEFAULT_MESSAGE
            (0 until plotArray.length()).mapNotNull { i ->
                plotArray.getJSONObject(i).takeIf { it.optString("plotLang") == "한국어" }
            }.firstOrNull()?.optString("plotText") ?: DEFAULT_MESSAGE
        } catch (e: JSONException) {
            DEFAULT_MESSAGE
        }
    }

    private fun safeParseActorsWithDefault(jsonMovie: JSONObject): String {
        return try {
            val actorsArray = jsonMovie.optJSONObject("actors")?.optJSONArray("actor") ?: return DEFAULT_MESSAGE
            (0 until Math.min(actorsArray.length(), 5))
                .joinToString(", ") { actorsArray.getJSONObject(it).optString("actorNm") }
                .takeIf { it != DEFAULT_MESSAGE } ?: DEFAULT_MESSAGE
        } catch (e: JSONException) {
            DEFAULT_MESSAGE
        }
    }

    private fun safeParseDirectorWithDefault(jsonMovie: JSONObject): String {
        return try {
            val directorsArray =
                jsonMovie.optJSONObject("directors")?.optJSONArray("director") ?: return DEFAULT_MESSAGE
            directorsArray.getJSONObject(0).optString("directorNm") ?: DEFAULT_MESSAGE
        } catch (e: JSONException) {
            DEFAULT_MESSAGE
        }
    }

    private fun getDataWithException(str: String): String {
        return str.takeIf { it.isNotEmpty() } ?: throw NoMovieException("제공하지 않는 영화입니다.")
    }

    private fun getDataWithDefault(input: String?, defaultString: String): String {
        return input?.takeIf { it.trim().isNotEmpty() } ?: defaultString
    }

    private fun parseMovieArrayFromJsonResponse(jsonResponse: JSONObject): JSONArray {
        return jsonResponse.getJSONArray("Data")
            .getJSONObject(0)
            .getJSONArray("Result")
    }

    private fun parseMoviesFromMovieArray(movieArray: JSONArray, result: MutableList<MovieSearchRespDTO?>) {
        for (i in 0 until movieArray.length()) {
            val movie = movieArray.getJSONObject(i)
            val id = getDataWithException(movie.optString("movieId") + "-" + movie.optString("movieSeq"))
            val title = getDataWithException(
                movie.optString("title")
                    .replace("!HS", "")
                    .replace("!HE", "")
                    .trim()
            )
            val imgUrl = getDataWithDefault(movie.optString("posters"), defaultImgUrl).split("|")[0]
            val producedYear = getDataWithDefault(movie.optString("prodYear"), DEFAULT_MESSAGE)
            val search = MovieSearchRespDTO(id, title, imgUrl, producedYear)
            result.add(search)
        }
    }
}

