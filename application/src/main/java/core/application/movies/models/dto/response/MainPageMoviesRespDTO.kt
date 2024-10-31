package core.application.movies.models.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "메인 페이지에 제공되는 영화 목록")
public class MainPageMoviesRespDTO {
	@Schema(description = "찜 많은 순 영화 목록")
	private List<MainPageMovieRespDTO> dibMovieList;

	@Schema(description = "평점 높은 순 영화 목록")
	private List<MainPageMovieRespDTO> ratingMovieList;

	@Schema(description = "리뷰 많은 순 영화 목록")
	private List<MainPageMovieRespDTO> reviewMovieList;

	public static MainPageMoviesRespDTO of(List<MainPageMovieRespDTO> dib,
		List<MainPageMovieRespDTO> rating,
		List<MainPageMovieRespDTO> review) {
		return MainPageMoviesRespDTO.builder()
			.dibMovieList(dib)
			.ratingMovieList(rating)
			.reviewMovieList(review)
			.build();
	}
}
