package core.application.security.auth

import core.application.users.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * 사용자 세부정보를 로드하는 서비스 클래스
 *
 * [UserDetailsService] 인터페이스를 구현하여,
 * 이메일을 사용하여 사용자 정보를 데이터베이스에서 검색
 * 사용자 정보가 존재하면 [CustomUserDetails] 객체를 반환,
 * 사용자가 존재하지 않는 경우 [UsernameNotFoundException] 예외 발생
 */
@Service
class CustomUserDetailsService
/**
 * [CustomUserDetailsService] 객체를 생성
 *
 * @param userRepository 사용자 정보를 관리하는 [UserRepository] 객체
 */(private val userRepository: UserRepository) : UserDetailsService {
    /**
     * 주어진 이메일을 사용하여 사용자 세부정보를 로드
     *
     * @param userEmail 사용자 이메일
     * @return [UserDetails] 사용자 세부정보
     * @throws UsernameNotFoundException 사용자가 존재하지 않을 경우 발생
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userEmail: String): UserDetails {
        val userEntity = userRepository.findByUserEmail(userEmail)
        if (userEntity.isPresent) {
            return CustomUserDetails(userEntity.get())
        } else {
            throw UsernameNotFoundException("해당 사용자를 찾을 수 없습니다.: $userEmail")
        }
    }
}
