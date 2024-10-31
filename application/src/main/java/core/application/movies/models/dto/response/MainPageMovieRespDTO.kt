package core.application.movies.models.dto.response;

import core.application.movies.models.entities.CachedMovieEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "메인 페이지의 제공되는 각 영화 정보")
public class MainPageMovieRespDTO {

	@Schema(description = "영화 ID", example = "A-12345")
	private String movieId;

	@Schema(description = "영화 제목", example = "범죄도시")
	private String title;

	@Schema(description = "영화 포스터 URL", example = "http://file.koreafilm.or.kr/thm/02/00/04/53/tn_DPK012845.jpg")
	private String posterUrl;

	@Schema(description = "영화 개봉일", example = "20151111")
	private String releaseDate;

	public static MainPageMovieRespDTO from(CachedMovieEntity movie) {
		return MainPageMovieRespDTO.builder()
			.movieId(movie.getMovieId())
			.title(movie.getTitle())
			.posterUrl(movie.getPosterUrl())
			.releaseDate(movie.getReleaseDate())
			.build();
	}
}
