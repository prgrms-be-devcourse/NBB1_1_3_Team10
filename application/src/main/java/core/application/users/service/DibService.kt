package core.application.users.service

import core.application.users.models.dto.DibRespDTO
import java.util.*

interface DibService {
    /*
        * 찜하기 기능
        * dib_table에 이미 찜 객체 존재 -> 찜 취소
        * dib_table에 찜 객체 존재 X -> 찜 생성
        * 이때 cached_movie_table에 찜하려는 객체가 없다면 추가
        * @return{"찜 취소 완료" / "찜 완료" 메시지, dibId, movieId 반환}
        */
    fun dibProcess(userId: UUID?, movieId: String?): DibRespDTO?
}
