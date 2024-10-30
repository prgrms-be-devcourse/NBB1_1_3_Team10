package core.application.users.service;

import core.application.users.models.dto.MessageResponseDTO;
import core.application.users.models.dto.SignupReqDTO;
import core.application.users.models.dto.UserUpdateReqDTO;
import core.application.users.models.entities.UserEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 관련 서비스를 정의하는 인터페이스
 *
 * 사용자 가입, 정보 업데이트, 삭제, 사용자 검색 기능을 제공
 * 사용자의 정보와 관련된 작업을 수행
 */

public interface UserService {

    public MessageResponseDTO signup (SignupReqDTO userRequestDTO);

    public MessageResponseDTO updateUserInfo (UserUpdateReqDTO userUpdateRequestDTO);

    public MessageResponseDTO updateUserInfoFromOAuth (UserUpdateReqDTO userUpdateRequestDTO, String userEmail);

    public MessageResponseDTO deleteUser ();

    public Optional<UserEntity> getUserByUserId(UUID userId);

    public Optional<UserEntity> getUserByUserEmail(String userEmail);
}
