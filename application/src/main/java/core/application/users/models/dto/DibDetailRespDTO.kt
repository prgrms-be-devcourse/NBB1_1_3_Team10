package core.application.users.models.dto

data class DibDetailRespDTO (
    /*
       * movieId : 찜한 영화 아이디
       * movieTitle : 찜한 영화 제목
       * moviePost : 찜한 영화 포스터
        */
    val movieId: String? = null,
    val movieTitle: String? = null,
    val moviePost: String? = null
)