package core.application.users.repositories.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;

public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
	Boolean existsByUserEmail(String email);

	Optional<UserEntity> findByUserEmail(String email);

	Optional<UserEntity> findByUserEmailAndUserPw(String email, String password);

	List<UserEntity> findByRole(UserRole role);
}
