package core.application.users.service

import core.application.users.models.dto.MessageResponseDTO
import core.application.users.models.dto.SignupReqDTO
import core.application.users.models.dto.UserUpdateReqDTO
import core.application.users.models.entities.UserEntity
import java.util.*

/**
 * 사용자 관련 서비스를 정의하는 인터페이스
 *
 * 사용자 가입, 정보 업데이트, 삭제, 사용자 검색 기능을 제공
 * 사용자의 정보와 관련된 작업을 수행
 */
interface UserService {
    fun signup(userRequestDTO: SignupReqDTO): MessageResponseDTO?

    fun updateUserInfo(userUpdateRequestDTO: UserUpdateReqDTO): MessageResponseDTO?

    fun updateUserInfoFromOAuth(userUpdateRequestDTO: UserUpdateReqDTO, userEmail: String): MessageResponseDTO?

    fun deleteUser(): MessageResponseDTO?

    fun getUserByUserId(userId: UUID?): Optional<UserEntity?>?

    fun getUserByUserEmail(userEmail: String?): Optional<UserEntity?>?
}
