package core.application.movies.repositories.movie

import core.application.movies.models.entities.CachedMovieEntity
import org.springframework.data.domain.Page
import java.util.*

/**
 * `CACHED_MOVIE_TABLE` 과 관련된 `Repository`
 */
interface CachedMovieRepository {
    // CREATE
    /**
     * 새로운 영화 정보를 DB 에 등록
     *
     * @param movie 새 영화 정보
     * @return [CachedMovieEntity] 등록된 영화 정보
     */
    fun saveNewMovie(movie: CachedMovieEntity?): CachedMovieEntity?

    //<editor-fold desc="READ">
    /**
     * 영화 ID 로 검색
     *
     * @param movieId 영화 ID
     * @return [Optional]`<`[CachedMovieEntity]`>`
     */
    fun findByMovieId(movieId: String?): Optional<CachedMovieEntity?>?

    /**
     * 캐시된 모든 영화를 찜 많은 순으로 검색
     *
     * @return [List]`<`[CachedMovieEntity]`>`
     */
    fun selectOnDibOrderDescend(): List<CachedMovieEntity?>?

    /**
     * 캐시된 영화를 찜 많은 순으로 `num` 개 검색
     *
     * @param num 가져올 영화 개수
     * @return [List]`<`[CachedMovieEntity]`>`
     */
    fun selectOnDibOrderDescend(num: Int): List<CachedMovieEntity?>?

    /**
     * 캐시된 모든 영화를 평점 높은 순으로 검색
     *
     * @return [List]`<`[CachedMovieEntity]`>`
     */
    fun selectOnAVGRatingDescend(): List<CachedMovieEntity?>?

    /**
     * 캐시된 영화를 평점 높은 순으로 `num` 개 검색
     *
     * @param num 가져올 영화 개수
     * @return [List]`<`[CachedMovieEntity]`>`
     */
    fun selectOnAVGRatingDescend(num: Int): List<CachedMovieEntity?>?

    //</editor-fold>
    /**
     * 캐시된 영화를 리뷰 많은 순으로 `num` 개 탐색
     *
     * @param num 가져올 영화 개수
     * @return [List]`<`[CachedMovieEntity]`>`
     */
    fun selectOnReviewCountDescend(num: Int): List<CachedMovieEntity?>?

    /**
     * JPA 구현체 사용 메서드.<br></br>
     * JPA를 이용해 페이징 관련 처리를 한다.
     * @param page 페이지
     * @param genre 장르
     * @return 해당 페이지의 평점순 장르 영화
     */
    fun findMoviesLikeGenreOrderByAvgRating(page: Int?, genre: String?): Page<CachedMovieEntity?>?

    // UPDATE
    /**
     * 특정 영화의 정보를 `replacement` 정보로 변경
     *
     * @param movieId     변경할 영화 ID
     * @param replacement 변경할 정보
     * @return [CachedMovieEntity] 변경된 정보
     */
    fun editMovie(movieId: String?, replacement: CachedMovieEntity?): CachedMovieEntity?

    // DELETE
    /**
     * 특정 영화를 삭제
     *
     * @param movieId 삭제할 영화 ID
     */
    fun deleteMovie(movieId: String?)
}
