package core.application.users.models.dto

import core.application.users.models.entities.UserEntity
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

data class UserUpdateReqDTO (
    val userEmail: @NotNull String? = null,

    var userPw: @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$",
        message = "비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다."
    ) String? = null,

    val userName: @Pattern(regexp = "^(?!.*\\s).+$", message = "별명에는 공백을 포함할 수 없습니다.") String? = null,

    val alias: @Pattern(regexp = "^(?!.*\\s).+$", message = "별명에는 공백을 포함할 수 없습니다.") String? = null,

    val phoneNum: @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 양식을 확인해주세요") String? = null
) {
    // UserDTO -> UserEntity 변환 메서드
    fun toEntity(): UserEntity {
        return UserEntity(
            null,
            this.userEmail,
            this.userPw,
            null,
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