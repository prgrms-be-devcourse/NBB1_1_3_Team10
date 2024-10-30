package core.application.movies.repositories.movie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import core.application.movies.models.entities.CachedMovieEntity;

/**
 * {@code CACHED_MOVIE_TABLE} 과 관련된 {@code Repository}
 */
public interface CachedMovieRepository {

	// CREATE

	/**
	 * 새로운 영화 정보를 DB 에 등록
	 *
	 * @param movie 새 영화 정보
	 * @return {@link CachedMovieEntity} 등록된 영화 정보
	 */
	CachedMovieEntity saveNewMovie(CachedMovieEntity movie);

	//<editor-fold desc="READ">

	/**
	 * 영화 ID 로 검색
	 *
	 * @param movieId 영화 ID
	 * @return {@link Optional}{@code <}{@link CachedMovieEntity}{@code >}
	 */
	Optional<CachedMovieEntity> findByMovieId(String movieId);

	/**
	 * 캐시된 모든 영화를 찜 많은 순으로 검색
	 *
	 * @return {@link List}{@code <}{@link CachedMovieEntity}{@code >}
	 */
	List<CachedMovieEntity> selectOnDibOrderDescend();

	/**
	 * 캐시된 영화를 찜 많은 순으로 {@code num} 개 검색
	 *
	 * @param num 가져올 영화 개수
	 * @return {@link List}{@code <}{@link CachedMovieEntity}{@code >}
	 */
	List<CachedMovieEntity> selectOnDibOrderDescend(int num);

	/**
	 * 캐시된 모든 영화를 평점 높은 순으로 검색
	 *
	 * @return {@link List}{@code <}{@link CachedMovieEntity}{@code >}
	 */
	List<CachedMovieEntity> selectOnAVGRatingDescend();

	/**
	 * 캐시된 영화를 평점 높은 순으로 {@code num} 개 검색
	 *
	 * @param num 가져올 영화 개수
	 * @return {@link List}{@code <}{@link CachedMovieEntity}{@code >}
	 */
	List<CachedMovieEntity> selectOnAVGRatingDescend(int num);
	//</editor-fold>

	/**
	 * 캐시된 영화를 리뷰 많은 순으로 {@code num} 개 탐색
	 *
	 * @param num 가져올 영화 개수
	 * @return {@link List}{@code <}{@link CachedMovieEntity}{@code >}
	 */
	List<CachedMovieEntity> selectOnReviewCountDescend(int num);

	/**
	 * JPA 구현체 사용 메서드.<br>
	 * JPA를 이용해 페이징 관련 처리를 한다.
	 * @param page 페이지
	 * @param genre 장르
	 * @return 해당 페이지의 평점순 장르 영화
	 */
	Page<CachedMovieEntity> findMoviesLikeGenreOrderByAvgRating(int page, String genre);

	// UPDATE

	/**
	 * 특정 영화의 정보를 {@code replacement} 정보로 변경
	 *
	 * @param movieId     변경할 영화 ID
	 * @param replacement 변경할 정보
	 * @return {@link CachedMovieEntity} 변경된 정보
	 */
	CachedMovieEntity editMovie(String movieId, CachedMovieEntity replacement);

	// DELETE

	/**
	 * 특정 영화를 삭제
	 *
	 * @param movieId 삭제할 영화 ID
	 */
	void deleteMovie(String movieId);
}
