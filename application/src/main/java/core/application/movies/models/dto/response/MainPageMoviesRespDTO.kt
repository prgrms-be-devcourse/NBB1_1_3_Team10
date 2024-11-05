package core.application.movies.models.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import lombok.Builder
import lombok.Data

@Data
@Builder
@Schema(description = "메인 페이지에 제공되는 영화 목록")
data class MainPageMoviesRespDTO(
    @Schema(description = "찜 많은 순 영화 목록")
    val dibMovieList: List<MainPageMovieRespDTO?>?,

    @Schema(description = "평점 높은 순 영화 목록")
    val ratingMovieList: List<MainPageMovieRespDTO?>?,

    @Schema(description = "리뷰 많은 순 영화 목록")
    val reviewMovieList: List<MainPageMovieRespDTO?>?
) {
    companion object {
        fun of(
            dib: List<MainPageMovieRespDTO?>?,
            rating: List<MainPageMovieRespDTO?>?,
            review: List<MainPageMovieRespDTO?>?
        ): MainPageMoviesRespDTO {
            return MainPageMoviesRespDTO(
                dibMovieList = dib,
                ratingMovieList = rating,
                reviewMovieList = review
            )
        }
    }
}


