package core.application.security.oauth

import core.application.security.model.GoogleResponse
import core.application.security.model.NaverResponse
import core.application.security.model.OAuth2Response
import core.application.users.models.dto.SignupReqDTO
import core.application.users.models.dto.UserDTO
import core.application.users.models.dto.UserUpdateReqDTO
import core.application.users.models.entities.UserRole
import core.application.users.service.UserService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

/**
 * OAuth2 사용자 정보를 로드하고 처리하는 서비스
 */
@Service
class CustomOAuth2UserService
/**
 * CustomOAuth2UserService 생성자
 *
 * @param userService 사용자 관련 서비스
 */(private val userService: UserService) : DefaultOAuth2UserService() {
    /**
     * OAuth2 사용자 정보를 로드
     *
     * @param oAuth2UserRequest OAuth2 사용자 요청
     * @return CustomOAuth2User 객체
     * @throws OAuth2AuthenticationException 인증 실패 시 예외 발생
     */
    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        // OAuth2 사용자 정보를 로드
        val oAuth2User = super.loadUser(oAuth2UserRequest)
        val oAuth2Response: OAuth2Response

        // OAuth 사용자 정보 공급자 ID
        val registrationId = oAuth2UserRequest.clientRegistration.registrationId

        // 공급자에 따라 적절한 OAuth2Response 객체를 생성
        oAuth2Response = if (registrationId == "naver") {
            NaverResponse(oAuth2User.attributes)
        } else if (registrationId == "google") {
            GoogleResponse(oAuth2User.attributes)
        } else {
            throw OAuth2AuthenticationException("invalid registration id $registrationId")
        }

        // OAuth 사용자 정보를 기반으로 비밀번호를 생성
        val password = oAuth2Response.provider + " " + oAuth2Response.providerId

        // DB에서 기존 사용자 정보를 조회
        val existedUser = userService.getUserByUserEmail(oAuth2Response.email)


        // OAuth로 인증된 사용자 DB에 저장
        if (existedUser!!.isEmpty) {
            val newUserDTO = SignupReqDTO(
                oAuth2Response.email,
                password,
                UserRole.USER,
                oAuth2Response.name,
                oAuth2Response.alias,
                null
            )

            newUserDTO.encodePassword()
            userService.signup(newUserDTO)

            val oAuth2UserDTO = UserDTO(
                null,
                oAuth2Response.email,
                password,
                UserRole.USER,
                oAuth2Response.alias,
                null,
                oAuth2Response.name
            )

            return CustomOAuth2User(oAuth2UserDTO)
        } else {
            val updatedUserDTO = UserUpdateReqDTO(
                existedUser.get().userEmail,
                password,
                oAuth2Response.name,
                existedUser.get().alias,
                existedUser.get().phoneNum
            )

            updatedUserDTO.encodePassword()
            userService.updateUserInfoFromOAuth(updatedUserDTO, existedUser.get().userEmail!!)

            val editedUserEntity = userService.getUserByUserEmail(existedUser.get().userEmail)

            val editedUserDTO = UserDTO(
                null,
                editedUserEntity!!.get().userEmail,
                editedUserEntity.get().userPw,
                UserRole.USER,
                null,
                null,
                editedUserEntity.get().userName
            )

            return CustomOAuth2User(editedUserDTO)
        }
    }
}
