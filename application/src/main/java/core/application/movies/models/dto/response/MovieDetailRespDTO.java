package core.application.movies.models.dto.response;

import core.application.movies.models.entities.CachedMovieEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "영화 상세 페이지 정보")
@AllArgsConstructor
public class MovieDetailRespDTO {
	@Schema(description = "영화 ID", example = "A-12345")
	private String movieId;

	@Schema(description = "영화 제목", example = "베테랑2")
	private String title;

	@Schema(description = "영화 포스터 URL", example = "http://file.koreafilm.or.kr/thm/02/00/04/53/tn_DPK012845.jpg")
	private String posterUrl;

	@Schema(description = "영화 장르", example = "액션, 범죄")
	private String genre;

	@Schema(description = "영화 개봉일", example = "20151111")
	private String releaseDate;

	@Schema(description = "영화 줄거리", example = "\"오늘 밤, 다 쓸어버린다!\"2004년 서울…하얼빈에서 넘어와 단숨에 기존 조직들을 장악하고 가장 강력한 세력인..")
	private String plot;

	@Schema(description = "영화 상영시간", example = "121")
	private String runningTime;

	@Schema(description = "영화 배우", example = "마동석, 윤계상..")
	private String actors;

	@Schema(description = "영화 감독", example = "강운성")
	private String director;

	@Schema(description = "영화 찜 개수", example = "87")
	private Long dibCount;

	@Schema(description = "영화 리뷰 개수", example = "10")
	private Long reviewCount;

	@Schema(description = "영화 한줄평 개수", example = "176")
	private Long commentCount;

	@Schema(description = "영화 한줄평 점수 총합", example = "1658")
	private Long sumOfRating;

	public static MovieDetailRespDTO from(CachedMovieEntity cachedMovieEntity) {
		return MovieDetailRespDTO.builder()
			.movieId(cachedMovieEntity.getMovieId())
			.title(cachedMovieEntity.getTitle())
			.posterUrl(cachedMovieEntity.getPosterUrl())
			.genre(cachedMovieEntity.getGenre())
			.releaseDate(cachedMovieEntity.getReleaseDate())
			.plot(cachedMovieEntity.getPlot())
			.runningTime(cachedMovieEntity.getRunningTime())
			.actors(cachedMovieEntity.getActors())
			.director(cachedMovieEntity.getDirector())
			.dibCount(cachedMovieEntity.getDibCount())
			.reviewCount(cachedMovieEntity.getReviewCount())
			.commentCount(cachedMovieEntity.getCommentCount())
			.sumOfRating(cachedMovieEntity.getSumOfRating())
			.build();
	}
}
