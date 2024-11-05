package core.application.users.service;

import core.application.security.service.AuthenticatedUserService;
import core.application.users.models.dto.MessageResponseDTO;
import core.application.users.models.dto.SignupReqDTO;
import core.application.users.models.dto.UserDTO;
import core.application.users.models.dto.UserUpdateReqDTO;
import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import core.application.users.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticatedUserService authenticatedUserService;

    @InjectMocks
    private UserServiceImpl userService;

    SignupReqDTO signupReqDTO;

    UserUpdateReqDTO userUpdateReqDTO;

    UserEntity userEntity;

    @BeforeEach
    public void init() {
        signupReqDTO = new SignupReqDTO(
                "anyone@naver.com",
                "ilive123!123",
                UserRole.USER,
                "anyany",
                "anyone",
                "010-1111-1111"
        );

        userUpdateReqDTO = new UserUpdateReqDTO(
                "anyone@naver.com",
                "ilive123!123",
                "anyany",
                "anyone",
                "010-1111-1111"
        );

        userEntity = new UserEntity(
                null,
                "anyone@naver.com",
                "ilive123!123",
                UserRole.USER,
                "anyone",
                "010-1111-1111",
                "anyany"
        );
    }

    @Test
    @DisplayName("회원 가입")
    public void userService_signup_returnMessageResponseDTO() {
        when(userRepository.saveNewUser(Mockito.any(UserEntity.class))).thenReturn(userEntity);
        when(userRepository.findByUserEmail(signupReqDTO.getUserEmail())).thenReturn(Optional.of(userEntity));

        MessageResponseDTO messageResponseDTO = userService.signup(signupReqDTO);

        Assertions.assertThat(messageResponseDTO).isNotNull();
    }

    @Test
    @DisplayName("회원 정보 변경")
    public void userService_updateUser_returnMessageResponseDTO() {
        when(authenticatedUserService.getAuthenticatedUserEmail()).thenReturn(userUpdateReqDTO.getUserEmail());
        when(userRepository.findByUserEmail(signupReqDTO.getUserEmail())).thenReturn(Optional.of(userEntity));
        when(userRepository.editUserInfo(Mockito.any(UserEntity.class))).thenReturn(1);

        MessageResponseDTO messageResponseDTO = userService.updateUserInfo(userUpdateReqDTO);

        Assertions.assertThat(messageResponseDTO).isNotNull();
    }

    @Test
    @DisplayName("회원 정보 삭제")
    public void userService_deleteUser_returnMessageResponseDTO() {
        when(authenticatedUserService.getAuthenticatedUserId()).thenReturn(userEntity.getUserId());
        when(userRepository.deleteUser(userEntity.getUserId())).thenReturn(1);

        MessageResponseDTO messageResponseDTO = userService.deleteUser();

        Assertions.assertThat(messageResponseDTO).isNotNull();
    }

    @Test
    @DisplayName("사용자 id를 이용한 회원 정보 조회")
    public void userService_getUserByUserId_returnUserEntity() {
        when(userRepository.findByUserId(userEntity.getUserId())).thenReturn(Optional.of(userEntity));

        Optional<UserEntity> userEntityReturn = userService.getUserByUserId(userEntity.getUserId());

        Assertions.assertThat(userEntityReturn).isPresent();
    }

    @Test
    @DisplayName("사용자 email을 이용한 회원 정보 조회")
    public void userService_getUserByUserEmail_returnUserEntity() {
        when(userRepository.findByUserEmail(userEntity.getUserEmail())).thenReturn(Optional.of(userEntity));

        Optional<UserEntity> userEntityReturn = userService.getUserByUserEmail(userEntity.getUserEmail());

        Assertions.assertThat(userEntityReturn).isPresent();
    }
}