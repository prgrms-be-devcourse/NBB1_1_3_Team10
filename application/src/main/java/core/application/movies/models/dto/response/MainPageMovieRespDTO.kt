package core.application.movies.models.dto.response

import core.application.movies.models.entities.CachedMovieEntity
import io.swagger.v3.oas.annotations.media.Schema
import lombok.Builder
import lombok.Data

@Data
@Builder
@Schema(description = "메인 페이지의 제공되는 각 영화 정보")
class MainPageMovieRespDTO(
    @Schema(description = "영화 ID", example = "A-12345")
    val movieId: String? = null,

    @Schema(description = "영화 제목", example = "범죄도시")
    val title: String? = null,

    @Schema(description = "영화 포스터 URL", example = "http://file.koreafilm.or.kr/thm/02/00/04/53/tn_DPK012845.jpg")
    val posterUrl: String? = null,

    @Schema(description = "영화 개봉일", example = "20151111")
    val releaseDate: String? = null
) {
    companion object {
        @JvmStatic
        fun from(movie: CachedMovieEntity): MainPageMovieRespDTO {
            return MainPageMovieRespDTO(
                movieId = movie.movieId,
                title = movie.title,
                posterUrl = movie.posterUrl,
                releaseDate = movie.releaseDate
            )
        }
    }
}

