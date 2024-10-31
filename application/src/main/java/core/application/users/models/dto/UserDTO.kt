package core.application.users.models.dto

import core.application.users.models.entities.UserEntity
import core.application.users.models.entities.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.util.*

data class UserDTO (
    val userId: UUID? = null,

    val userEmail: @Email @Pattern(
        regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        message = "이메일 주소 양식을 확인해주세요."
    ) String? = null,

    var userPw: @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$",
        message = "비밀번호는 8자 이상 15자 이하이며, 영문자, 숫자 및 특수문자를 포함해야 합니다."
    ) String? = null,

    val role: UserRole? = null,

    val alias: @Pattern(regexp = "^(?!.*\\s).+$", message = "별명에는 공백을 포함할 수 없습니다.") String? = null,

    val phoneNum: @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 맞지 않습니다.") String? = null,

    val userName: @Pattern(regexp = "^(?!.*\\s).+$", message = "이름에는 공백을 포함할 수 없습니다.") String? = null
) {
    // UserDTO -> UserEntity 변환 메서드
    fun toEntity(): UserEntity {
        return UserEntity(
            this.userId,
            this.userEmail,
            this.userPw,
            this.role,
            this.alias,
            this.phoneNum,
            this.userName
        )
    }

    fun encodePassword() {
        val bCryptPasswordEncoder = BCryptPasswordEncoder()
        this.userPw = bCryptPasswordEncoder.encode(this.userPw)
    }
}
