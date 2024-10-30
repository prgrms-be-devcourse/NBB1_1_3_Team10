package core.application.users.repositories;

import static org.assertj.core.api.Assertions.*;

import core.application.users.models.entities.*;
import java.util.*;
import java.util.function.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.transaction.annotation.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepo;

    private static UserEntity testUser; // USER
    private static UserEntity testUser2; // ADMIN


    @BeforeEach
    void init() {

        testUser = UserEntity.builder()
                .userEmail("test@test.com")
                .userPw("test")
                .role(UserRole.USER)
                .alias("소은")
                .phoneNum("010-0000-0000")
                .userName("정소은")
                .build();

        testUser2 = UserEntity.builder()
                .userEmail("test2@test.com")
                .userPw("test")
                .role(UserRole.ADMIN)
                .alias("소은")
                .phoneNum("010-0000-0000")
                .userName("정소은")
                .build();

    }

    @Test
    @Transactional
    @DisplayName("회원가입 / 새로운 유저 저장")
    void saveNewUser() {
        // Given

        // When
        userRepo.saveNewUser(testUser);
        UserEntity user = userRepo.findByUserEmail(testUser.getUserEmail()).orElseThrow();

        // Then
        Optional<UserEntity> find = userRepo.findByUserId(user.getUserId());
        checkEqualUser(find, user);
    }

    @Test
    @Transactional
    @DisplayName("유저 ID로 유저 찾기")
    void findByUserId() {
        // Given
        UserEntity save = userRepo.saveNewUser(testUser);

        // When
        Optional<UserEntity> find = userRepo.findByUserId(save.getUserId());
        checkEqualUser(find, save);
    }

    @Test
    @Transactional
    @DisplayName("유저 이메일로 유저 찾기")
    void findByUserEmail() {
        // Given
        UserEntity test = userRepo.saveNewUser(testUser);

        // When
        Optional<UserEntity> find = userRepo.findByUserEmail(testUser.getUserEmail());
        checkEqualUser(find, test);
    }

    @Test
    @Transactional
    @DisplayName("유저 이메일과 비밀번호로 유저 찾기 / 로그인")
    void findByUserEmailAndPassword() {
        // Given
        UserEntity save = userRepo.saveNewUser(testUser);

        // When
        Optional<UserEntity> find = userRepo.findByUserEmailAndPassword(testUser.getUserEmail(),
                testUser.getUserPw());

        checkEqualUser(find, save);
    }

    @Test
    @Transactional
    @DisplayName("해당 역할의 유저들 찾기")
    void findByUserRole() {
        // Given
        UserEntity test1 = userRepo.saveNewUser(testUser);
        UserEntity test2 = userRepo.saveNewUser(testUser2);

        // When
        List<UserEntity> findList1 = userRepo.findByUserRole(UserRole.USER);
        List<UserEntity> findList2 = userRepo.findByUserRole(UserRole.ADMIN);

        // Then
        findList1.forEach(r -> assertThat(r.getRole()).isEqualTo(UserRole.USER));
        findList2.forEach(r -> assertThat(r.getRole()).isEqualTo(UserRole.ADMIN));

        assertThat(findList1).contains(test1);
        assertThat(findList2).contains(test2);
    }

    @Test
    @Transactional
    @DisplayName("전체 유저 목록 조회")
    void findAll() {
        // Given
        UserEntity test1 = userRepo.saveNewUser(testUser);
        UserEntity test2 = userRepo.saveNewUser(testUser2);

        // When
        List<UserEntity> users = userRepo.findAll();

        // Then
        assertThat(users).contains(test1, test2);
    }

    @Test
    @Transactional
    @DisplayName("유저 정보 수정하기")
    void editUserInfo() {
        // Given
        UserEntity save = userRepo.saveNewUser(testUser);

        UserEntity editUser = UserEntity.builder()
                .userId(save.getUserId())
                .userEmail(testUser.getUserEmail())
                .userPw("editPw")
                .role(UserRole.USER)
                .alias("소소은")
                .phoneNum("010-1111-1111")
                .userName("정소은")
                .build();

        // When
        userRepo.editUserInfo(editUser);

        // Then
        Optional<UserEntity> find = userRepo.findByUserId(save.getUserId());
        checkEqualUser(find, editUser);
    }

    @Test
    @Transactional
    @DisplayName("유저 삭제하기")
    void deleteUser() {
        // Given
        userRepo.saveNewUser(testUser);

        // When
        UUID userId = testUser.getUserId();
        userRepo.deleteUser(userId);

        // Then
        assertThat(userRepo.findByUserId(userId)).isEmpty();

    }

    private interface TripleConsumer<T1, T2, T3> {

        void accept(T1 t1, T2 t2, T3 t3);
    }

    private void checkEqualUser(Optional<UserEntity> find, UserEntity user) {
        UserEntity value = find.orElseThrow();

        TripleConsumer<UserEntity, UserEntity,
                Function<UserEntity, Object>> checkEquality
                = (a, b, func) -> assertThat(func.apply(a)).isEqualTo(func.apply(b)).isNotNull();

        checkEquality.accept(value, user, UserEntity::getUserId);
        checkEquality.accept(value, user, UserEntity::getUserEmail);
        checkEquality.accept(value, user, UserEntity::getUserPw);
        checkEquality.accept(value, user, UserEntity::getRole);
        checkEquality.accept(value, user, UserEntity::getAlias);
        checkEquality.accept(value, user, UserEntity::getPhoneNum);
        checkEquality.accept(value, user, UserEntity::getUserName);
    }
}
