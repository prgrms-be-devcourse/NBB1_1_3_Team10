package core.application.security.oauth;

import core.application.security.model.GoogleResponse;
import core.application.security.model.NaverResponse;
import core.application.security.model.OAuth2Response;
import core.application.users.models.dto.SignupReqDTO;
import core.application.users.models.dto.UserDTO;
import core.application.users.models.dto.UserUpdateReqDTO;
import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import core.application.users.service.UserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * OAuth2 사용자 정보를 로드하고 처리하는 서비스
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;


    /**
     * CustomOAuth2UserService 생성자
     *
     * @param userService 사용자 관련 서비스
     */
    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * OAuth2 사용자 정보를 로드
     *
     * @param oAuth2UserRequest OAuth2 사용자 요청
     * @return CustomOAuth2User 객체
     * @throws OAuth2AuthenticationException 인증 실패 시 예외 발생
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        // OAuth2 사용자 정보를 로드
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        OAuth2Response oAuth2Response;

        // OAuth 사용자 정보 공급자 ID
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        // 공급자에 따라 적절한 OAuth2Response 객체를 생성
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {
            throw new OAuth2AuthenticationException("invalid registration id " + registrationId);
        }

        // OAuth 사용자 정보를 기반으로 비밀번호를 생성
        String password = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        // DB에서 기존 사용자 정보를 조회
        Optional <UserEntity> existedUser = userService.getUserByUserEmail(oAuth2Response.getEmail());


        // OAuth로 인증된 사용자 DB에 저장
        if (existedUser.isEmpty()) {
            SignupReqDTO newUserDTO = new SignupReqDTO(
                    oAuth2Response.getEmail(),
                    password,
                    UserRole.USER,
                    oAuth2Response.getName(),
                    oAuth2Response.getAlias(),
                    null
            );

            newUserDTO.encodePassword();
            userService.signup(newUserDTO);

            UserDTO oAuth2UserDTO = UserDTO.builder()
                    .userName(oAuth2Response.getName())
                    .userEmail(oAuth2Response.getEmail())
                    .userPw(password)
                    .role(UserRole.USER)
                    .alias(oAuth2Response.getAlias())
                    .build();

            return new CustomOAuth2User(oAuth2UserDTO);
        }

        // OAuth로 인증된 사용자 정보를 기존의 DB에 갱신
        else {
            UserUpdateReqDTO updatedUserDTO = new UserUpdateReqDTO(
                    existedUser.get().getUserEmail(),
                    password,
                    oAuth2Response.getName(),
                    existedUser.get().getAlias(),
                    existedUser.get().getPhoneNum());

            updatedUserDTO.encodePassword();
            userService.updateUserInfoFromOAuth(updatedUserDTO, existedUser.get().getUserEmail());

            Optional <UserEntity> editedUserEntity = userService.getUserByUserEmail(existedUser.get().getUserEmail());

            UserDTO editedUserDTO = UserDTO.builder()
                    .userName(editedUserEntity.get().getUserName())
                    .userPw(editedUserEntity.get().getUserPw())
                    .userEmail(editedUserEntity.get().getUserEmail())
                    .role(UserRole.USER)
                    .build();

            return new CustomOAuth2User(editedUserDTO);
        }
    }
}
