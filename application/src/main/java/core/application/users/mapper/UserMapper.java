package core.application.users.mapper;

import core.application.users.models.entities.DibEntity;
import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface UserMapper {
    int saveNewUser(@Param("newUser")UserEntity newUser);
    Optional<UserEntity> findByUserId(UUID userId);
    Optional<UserEntity> findByUserEmail(String email);
    Optional<UserEntity> findByUserEmailAndPassword(String userEmail, String userPw);
    List<UserEntity> findByUserRole(UserRole role);
    List<UserEntity> findAll();
    int editUserInfo(@Param("replacement") UserEntity replacement);
    int deleteUser(UUID userId);

}
