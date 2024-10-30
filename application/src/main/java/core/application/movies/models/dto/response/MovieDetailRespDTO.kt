package core.application.movies.models.dto.response

import core.application.movies.models.entities.CachedMovieEntity
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "영화 상세 페이지 정보")
data class MovieDetailRespDTO(
    @Schema(description = "영화 ID", example = "A-12345")
    val movieId: String? = null,

    @Schema(description = "영화 제목", example = "베테랑2")
    val title: String? = null,

    @Schema(description = "영화 포스터 URL", example = "http://file.koreafilm.or.kr/thm/02/00/04/53/tn_DPK012845.jpg")
    val posterUrl: String? = null,

    @Schema(description = "영화 장르", example = "액션, 범죄")
    val genre: String? = null,

    @Schema(description = "영화 개봉일", example = "20151111")
    val releaseDate: String? = null,

    @Schema(description = "영화 줄거리", example = "\"오늘 밤, 다 쓸어버린다!\"2004년 서울…하얼빈에서 넘어와 단숨에 기존 조직들을 장악하고 가장 강력한 세력인..")
    val plot: String? = null,

    @Schema(description = "영화 상영시간", example = "121")
    val runningTime: String? = null,

    @Schema(description = "영화 배우", example = "마동석, 윤계상..")
    val actors: String? = null,

    @Schema(description = "영화 감독", example = "강운성")
    val director: String? = null,

    @Schema(description = "영화 찜 개수", example = "87")
    val dibCount: Long? = null,

    @Schema(description = "영화 리뷰 개수", example = "10")
    val reviewCount: Long? = null,

    @Schema(description = "영화 한줄평 개수", example = "176")
    val commentCount: Long? = null,

    @Schema(description = "영화 한줄평 점수 총합", example = "1658")
    val sumOfRating: Long? = null
) {
    companion object {
        fun from(cachedMovieEntity: CachedMovieEntity): MovieDetailRespDTO {
            return MovieDetailRespDTO(
                movieId = cachedMovieEntity.movieId,
                title = cachedMovieEntity.title,
                posterUrl = cachedMovieEntity.posterUrl,
                genre = cachedMovieEntity.genre,
                releaseDate = cachedMovieEntity.releaseDate,
                plot = cachedMovieEntity.plot,
                runningTime = cachedMovieEntity.runningTime,
                actors = cachedMovieEntity.actors,
                director = cachedMovieEntity.director,
                dibCount = cachedMovieEntity.dibCount,
                reviewCount = cachedMovieEntity.reviewCount,
                commentCount = cachedMovieEntity.commentCount,
                sumOfRating = cachedMovieEntity.sumOfRating
            )
        }
    }
}
