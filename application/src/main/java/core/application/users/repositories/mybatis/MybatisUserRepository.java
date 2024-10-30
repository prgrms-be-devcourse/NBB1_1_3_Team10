package core.application.users.repositories.mybatis;

import core.application.users.exception.UserNotFoundException;
import core.application.users.mapper.UserMapper;
import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import core.application.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 사용자 정보를 처리하는 {@link UserRepository}의 구현체
 *
 * 사용자 정보에 대한 CRUD 작업을 수행하고
 * {@link UserMapper}를 사용하여 데이터베이스와 상호작용함
 */
@Repository
@RequiredArgsConstructor
@Profile("mybatis")
public class MybatisUserRepository implements UserRepository{

    private final UserMapper mapper;


    @Override
    public UserEntity saveNewUser(UserEntity newUser) {
        mapper.saveNewUser(newUser);
        return mapper.findByUserEmail(newUser.getUserEmail())
            .orElseThrow(() -> new UserNotFoundException("저장에 실패했습니다."));
    }

    @Override
    public Optional<UserEntity> findByUserId(UUID userId) {
        return mapper.findByUserId(userId);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return mapper.findByUserEmail(email).isPresent();
    }

    @Override
    public Optional<UserEntity> findByUserEmail(String email) {
        return mapper.findByUserEmail(email);
    }

    @Override
    public Optional<UserEntity> findByUserEmailAndPassword(String userEmail, String userPw) {
        return mapper.findByUserEmailAndPassword(userEmail, userPw);
    }
    @Override
    public List<UserEntity> findByUserRole(UserRole role) {
        return mapper.findByUserRole(role);
    }

    @Override
    public List<UserEntity> findAll() {
        return mapper.findAll();
    }

    @Override
    public int editUserInfo(UserEntity replacement) {
        return mapper.editUserInfo(replacement);
    }

    @Override
    public int deleteUser(UUID userId) {
        return mapper.deleteUser(userId);
    }
}
