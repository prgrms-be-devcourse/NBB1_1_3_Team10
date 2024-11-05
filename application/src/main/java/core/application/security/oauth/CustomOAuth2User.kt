package core.application.security.oauth

import core.application.users.models.dto.UserDTO
import lombok.ToString
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User
import java.util.*

/**
 * OAuth2 인증을 위한 사용자 정보를 담는 클래스
 */
@ToString
class CustomOAuth2User(private val userDTO: UserDTO) : OAuth2User {
    override fun getAttributes(): Map<String, Any> {
        return java.util.Map.of()
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities: MutableCollection<GrantedAuthority> = ArrayList()

        authorities.add(GrantedAuthority { userDTO.role.toString() })
        return authorities
    }

    override fun getName(): String? {
        return userDTO.userName
    }

    val userEmail: String?
        get() = userDTO.userEmail

    val userId: UUID?
        get() = userDTO.userId

    val alias: String?
        get() = userDTO.alias

    val userRole: String
        get() = userDTO.role.toString()
}
