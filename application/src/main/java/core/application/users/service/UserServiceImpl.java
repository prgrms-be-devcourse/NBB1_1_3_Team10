package core.application.users.service;

import core.application.security.service.AuthenticatedUserService;
import core.application.users.exception.DuplicateEmailException;
import core.application.users.exception.UserNotFoundException;
import core.application.users.models.dto.MessageResponseDTO;
import core.application.users.models.dto.UserDTO;
import core.application.users.models.dto.SignupReqDTO;
import core.application.users.models.dto.UserUpdateReqDTO;
import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import core.application.users.repositories.UserRepository;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 서비스 구현 클래스
 * 사용자 회원가입, 정보 수정, 삭제 및 조회 기능을 제공
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserInfo;

    /**
     * 생성자
     *
     * @param userRepositoryImpl 사용자 리포지토리 구현체
     * @param authenticatedUserInfo 인증된 사용자 서비스
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepositoryImpl, AuthenticatedUserService authenticatedUserInfo) {
        this.userRepository = userRepositoryImpl;
        this.authenticatedUserInfo = authenticatedUserInfo;
	}

    /**
     * 사용자 회원가입 처리
     *
     * @param userRequestDTO 사용자 정보를 담고 있는 DTO
     * @return 회원가입 결과 메시지를 포함하는 MessageResponseDTO
     */
    @Override
    public MessageResponseDTO signup(SignupReqDTO userRequestDTO) {
        if (userRepository.existsByEmail(userRequestDTO.getUserEmail())) {
            throw new DuplicateEmailException("중복된 이메일입니다.");
        }
        userRequestDTO.encodePassword();
        UserEntity user = userRequestDTO.toEntity();
        userRepository.saveNewUser(user);
        Optional<UserEntity> userEntity = userRepository.findByUserEmail(userRequestDTO.getUserEmail());

        if (userEntity.isPresent()) {
            return new MessageResponseDTO(userEntity.get().getUserId(), "signUp success");
        } else {
            throw new UserNotFoundException("회원 가입에 실패했습니다.");
        }
    }

    /**
     * 사용자 정보 수정
     *
     * @param userUpdateRequestDTO 수정할 사용자 정보를 담고 있는 DTO
     * @return 수정 결과 메시지를 포함하는 MessageResponseDTO, 수정이 실패할 경우 예외 발생
     */
    @Override
    public MessageResponseDTO updateUserInfo(UserUpdateReqDTO userUpdateRequestDTO) {
        String userEmail = authenticatedUserInfo.getAuthenticatedUserEmail();

        // 요청 시 토큰의 userEmail과 다른 userEmail을 가지고 있는 사용자의 정보를 바꾸려고 할 때 예외 발생
        if (!userEmail.equals(userUpdateRequestDTO.getUserEmail())) {
            throw new UserNotFoundException("해당 사용자의 정보를 수정할 수 없습니다: 권한이 없습니다.");
        }

        Optional<UserEntity> originUserEntity = userRepository.findByUserEmail(userUpdateRequestDTO.getUserEmail());
        if (originUserEntity.isEmpty()) {
            throw new UserNotFoundException("기존에 입력된 회원 정보가 존재하지 않습니다.");
        }

        // 새로운 UserEntity를 기존 값과 DTO 값을 비교하여 생성
        UserDTO updatedUserDTO = new UserDTO(
                originUserEntity.get().getUserId(), // 기존 userId 유지 -> update 할 사용자 찾을 때 사용
                originUserEntity.get().getUserEmail(),
                userUpdateRequestDTO.getUserPw() != null ? userUpdateRequestDTO.getUserPw() : originUserEntity.get().getUserPw(), // userPw 업데이트
                originUserEntity.get().getRole(),
                userUpdateRequestDTO.getAlias() != null ? userUpdateRequestDTO.getAlias() : originUserEntity.get().getAlias(), // alias 업데이트
                userUpdateRequestDTO.getPhoneNum() != null ? userUpdateRequestDTO.getPhoneNum() : originUserEntity.get().getPhoneNum(), // phoneNum 업데이트
                userUpdateRequestDTO.getUserName() != null ? userUpdateRequestDTO.getUserName() : originUserEntity.get().getUserName() // userName 업데이트
        );

        System.out.println(updatedUserDTO);
        updatedUserDTO.encodePassword();

        if (userRepository.editUserInfo(updatedUserDTO.toEntity()) == 1) {
            return new MessageResponseDTO(originUserEntity.get().getUserId(), "update success");
        }
        throw new UserNotFoundException("회원 정보 수정에 실패했습니다.");
    }

    /**
     * 사용자 정보 수정
     *
     * @param userUpdateRequestDTO 수정할 사용자 정보를 담고 있는 DTO
     * @return 수정 결과 메시지를 포함하는 MessageResponseDTO, 수정이 실패할 경우 예외 발생
     */
    @Override
    public MessageResponseDTO updateUserInfoFromOAuth(UserUpdateReqDTO userUpdateRequestDTO, String userEmail) {
        // 요청 시 토큰의 userEmail과 다른 userEmail을 가지고 있는 사용자의 정보를 바꾸려고 할 때 예외 발생
        if (!userEmail.equals(userUpdateRequestDTO.getUserEmail())) {
            throw new UserNotFoundException("해당 사용자의 정보를 수정할 수 없습니다: 권한이 없습니다.");
        }

        Optional<UserEntity> originUserEntity = userRepository.findByUserEmail(userUpdateRequestDTO.getUserEmail());
        if (originUserEntity.isEmpty()) {
            throw new UserNotFoundException("기존에 입력된 회원 정보가 존재하지 않습니다.");
        }

        // 새로운 UserEntity를 기존 값과 DTO 값을 비교하여 생성
        UserDTO updatedUserDTO = new UserDTO(
                originUserEntity.get().getUserId(), // 기존 userId 유지
                originUserEntity.get().getUserEmail(),
                userUpdateRequestDTO.getUserPw() != null ? userUpdateRequestDTO.getUserPw() : originUserEntity.get().getUserPw(), // userPw 업데이트
                originUserEntity.get().getRole(),
                userUpdateRequestDTO.getAlias() != null ? userUpdateRequestDTO.getAlias() : originUserEntity.get().getAlias(), // alias 업데이트
                userUpdateRequestDTO.getPhoneNum() != null ? userUpdateRequestDTO.getPhoneNum() : originUserEntity.get().getPhoneNum(), // phoneNum 업데이트
                userUpdateRequestDTO.getUserName() != null ? userUpdateRequestDTO.getUserName() : originUserEntity.get().getUserName() // userName 업데이트
        );

        updatedUserDTO.encodePassword();

        if (userRepository.editUserInfo(updatedUserDTO.toEntity()) == 1) {
            return new MessageResponseDTO(originUserEntity.get().getUserId(), "update success");
        }
        throw new UserNotFoundException("회원 정보 수정에 실패했습니다.");
    }

    /**
     * 현재 인증된 사용자 계정 삭제
     *
     * @return 삭제 결과 메시지를 포함하는 MessageResponseDTO, 삭제가 실패할 경우 예외 발생
     */
    @Override
    public MessageResponseDTO deleteUser() {
        UUID userId = authenticatedUserInfo.getAuthenticatedUserId();
        if (userRepository.deleteUser(userId) == 1) {
            return new MessageResponseDTO(userId, "delete success");
        }
        throw new UserNotFoundException("사용자 삭제를 실패했습니다.");
    }

    /**
     * 사용자 ID로 사용자 정보를 조회
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 엔티티를 포함하는 Optional, 사용자 정보를 찾지 못한 경우 빈 Optional 반환
     */
    public Optional<UserEntity> getUserByUserId(UUID userId) {
        return userRepository.findByUserId(userId);
    }

    /**
     * 사용자 이메일로 사용자 정보를 조회
     *
     * @param userEmail 조회할 사용자 이메일
     * @return 사용자 엔티티를 포함하는 Optional, 사용자 정보를 찾지 못한 경우 빈 Optional 반환
     */
    public Optional<UserEntity> getUserByUserEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }
}
