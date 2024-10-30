package core.application.users.repositories.jpa.repositories;

import core.application.users.models.entities.*;
import core.application.users.repositories.*;
import core.application.users.repositories.jpa.*;
import java.util.*;
import lombok.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;

@Repository
@RequiredArgsConstructor
@Profile("jpa")
public class UserRepositoryJpaImpl implements UserRepository {

    private final JpaUserRepository jpaRepository;

    @Override
    public UserEntity saveNewUser(UserEntity newUser) {
        return jpaRepository.save(newUser);
    }

    @Override
    public Optional<UserEntity> findByUserId(UUID userId) {
        return jpaRepository.findById(userId);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return jpaRepository.existsByUserEmail(email);
    }

    @Override
    public Optional<UserEntity> findByUserEmail(String email) {
        return jpaRepository.findByUserEmail(email);
    }

    @Override
    public Optional<UserEntity> findByUserEmailAndPassword(String email, String password) {
        return jpaRepository.findByUserEmailAndUserPw(email, password);
    }

    @Override
    public List<UserEntity> findByUserRole(UserRole role) {
        return jpaRepository.findByRole(role);
    }

    @Override
    public List<UserEntity> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public int editUserInfo(UserEntity replacement) {
        if (jpaRepository.existsById(replacement.getUserId())) {
            jpaRepository.save(replacement);
            return 1;
        }
        return 0;
    }

    @Override
    public int deleteUser(UUID userId) {
        jpaRepository.deleteById(userId);
        return jpaRepository.findById(userId).isEmpty() ? 1 : 0;
    }
}
