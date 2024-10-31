package core.application.security.auth

import core.application.users.models.entities.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

/**
 * 사용자 세부정보를 담고 있는 클래스
 *
 *
 * Spring Security에서 사용자의 정보를 담기 위해 구현된 [UserDetails] 인터페이스
 * 사용자의 인증 정보(이메일, 비밀번호, 권한 등)를 제공
 */
@JvmRecord
data class CustomUserDetails(val userEntity: UserEntity) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val collection: MutableCollection<GrantedAuthority> = ArrayList()

        collection.add(GrantedAuthority { userEntity.role.toString() })

        return collection
    }

    override fun getPassword(): String? {
        return userEntity.userPw
    }

    override fun getUsername(): String? {
        return userEntity.userName
    }

    val userEmail: String
        get() = userEntity.userEmail.toString()

    val userId: UUID?
        get() = userEntity.userId

    val userRole: String
        get() = userEntity.role.toString()

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}