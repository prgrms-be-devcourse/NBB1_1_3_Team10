package core.application.movies.models.dto.response

import core.application.movies.models.entities.CachedMovieEntity
import io.swagger.v3.oas.annotations.media.Schema
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data

@Schema(description = "영화 검색 응답 정보")
data class MovieSearchRespDTO(
    @Schema(description = "영화 ID", example = "A-12345")
    val movieId: String? = null,

    @Schema(description = "영화 제목", example = "범죄도시")
    val title: String? = null,

    @Schema(description = "포스터 URL", example = "포스터 URL")
    val posterUrl: String? = null,

    @Schema(description = "제작년도", example = "2015")
    val producedYear: String? = null,
) {
    companion object {
        fun from(movie: CachedMovieEntity): MovieSearchRespDTO {
            return MovieSearchRespDTO(
                movieId = movie.movieId,
                title = movie.title,
                posterUrl = movie.posterUrl,
                producedYear = movie.releaseDate
            )
        }
    }
}

