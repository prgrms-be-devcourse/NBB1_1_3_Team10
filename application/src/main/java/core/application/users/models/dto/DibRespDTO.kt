package core.application.users.models.dto

import java.util.*

data class DibRespDTO (
    /*
       * message : 찜 삭제 완료 메시지
       * userId : 유저 아이디
       * movieId : 찜 삭제한 영화 아이디
        */
    val message: String? = null,
    val userId: UUID? = null,
    val movieId: String? = null
)