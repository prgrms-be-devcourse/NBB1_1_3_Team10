package core.application.users.repositories;

import static java.util.Comparator.*;
import static org.assertj.core.api.Assertions.*;

import core.application.movies.models.entities.*;
import core.application.movies.repositories.movie.*;
import core.application.users.models.entities.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.transaction.annotation.*;

@SpringBootTest
@Transactional
class DibRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(DibRepositoryTest.class);

    @Autowired
    private DibRepository dibRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CachedMovieRepository movieRepo;

    // 테스트용 엔티티
    private static final String TESTING = "TESTING";
    private static UserEntity testUser = genUser(TESTING);
    private static CachedMovieEntity testMovie = genMovie(TESTING);

    private static UserEntity genUser(String email) {
        return UserEntity.builder()
                .userEmail(email)
                .userPw(TESTING)
                .role(UserRole.USER)
                .userName(TESTING)
                .build();
    }

    private static CachedMovieEntity genMovie(String movieId) {
        return CachedMovieEntity.builder()
                .movieId(movieId)
                .title(TESTING)
                .posterUrl(TESTING)
                .genre(TESTING)
                .releaseDate(TESTING)
                .plot(TESTING)
                .runningTime("1234")    // DB 에 길이 제한 있어서 이렇게
                .actors(TESTING)
                .director(TESTING)
                .dibCount(0L)
                .reviewCount(0L)
                .commentCount(0L)
                .sumOfRating(0L)
                .build();
    }

    private static List<CachedMovieEntity> testMovieList;
    private static final int TEST_SIZE = 100;

    @BeforeEach
    void setUp() {
        testUser = userRepo.findByUserEmail(testUser.getUserEmail()).orElseGet(
                () -> userRepo.saveNewUser(testUser)
        );

        testMovie = movieRepo.findByMovieId(testMovie.getMovieId()).orElseGet(
                () -> movieRepo.saveNewMovie(testMovie)
        );

        testMovieList = IntStream.range(0, TEST_SIZE).parallel()
                .boxed()
                .map(i -> genMovie(TESTING + i))
                .toList();
    }

    @Test
    @DisplayName("새로운 찜 목록을 생성")
    void saveNewDib() {
        log.info("<- saveNewDib");

        DibEntity result = dibRepo.saveNewDib(testUser.getUserId(), testMovie.getMovieId());

        log.info(testUser.getUserId().toString());

        assertThat(result).satisfies(
                r -> assertThat(r).isNotNull(),
                r -> assertThat(r.getDibId()).isNotNull(),
                r -> assertThat(r.getUserId()).isEqualTo(testUser.getUserId()),
                r -> assertThat(r.getMovieId()).isEqualTo(testMovie.getMovieId())
        );

        log.info("-> saveNewDib");
    }

    @Test
    @DisplayName("찜 ID 로 검색")
    void findByDibId() {
        log.info("<- findByDibId");

        DibEntity test = dibRepo.saveNewDib(testUser.getUserId(), testMovie.getMovieId());

        DibEntity result = dibRepo.findByDibId(test.getDibId()).orElseThrow();

        assertThat(result).isEqualTo(test);

        log.info("-> findByDibId");
    }

    @Test
    @DisplayName("특정 유저의 찜 목록을 불러오기")
    void findByUserId() {
        log.info("<- findByUserId");

        // DB 에 영화 저장 + Dib 저장 후 반환
        List<DibEntity> test = testMovieList.stream()
                .map(t -> movieRepo.saveNewMovie(t))
                .map(t -> dibRepo.saveNewDib(testUser.getUserId(), t.getMovieId()))
                .sorted(comparing(DibEntity::getDibId))
                .toList();

        // DB 에 검색
        List<DibEntity> result = dibRepo.findByUserId(testUser.getUserId())
                .stream().sorted(comparing(DibEntity::getDibId))
                .toList();

        // 검증
        assertThat(result).hasSize(test.size());
        assertThat(result).containsAll(test);

        // 없을 땐 empty
        assertThat(dibRepo.findByUserId(UUID.randomUUID())).isEmpty();

        log.info("-> findByUserId");
    }

    @Test
    @DisplayName("특정 유저의 특정 찜을 영화 ID 로 불러오기")
    void findByUserIdAndMovieId() {
        log.info("<- findByUserIdAndMovieId");

        // DB 에 영화 & Dib 저장
        testMovieList.stream()
                .map(t -> movieRepo.saveNewMovie(t))
                .forEach(t -> dibRepo.saveNewDib(testUser.getUserId(), t.getMovieId()));

        // 타겟 찜 저장
        // testMovie 는 @BeforeEach 에서 이미 저장됨
        // movieRepo.saveNewMovie(testMovie);
        DibEntity test = dibRepo.saveNewDib(testUser.getUserId(), testMovie.getMovieId());

        Optional<DibEntity> result = dibRepo.findByUserIdAndMovieId(
                testUser.getUserId(), testMovie.getMovieId());

        // 검증
        assertThat(result).contains(test);

        // 없을 땐 empty
        assertThat(dibRepo.findByUserIdAndMovieId(UUID.randomUUID(), testMovie.getMovieId()))
                .isEmpty();
        assertThat(dibRepo.findByUserIdAndMovieId(testUser.getUserId(), TESTING + TESTING))
                .isEmpty();

        log.info("-> findByUserIdAndMovieId");
    }

    @Test
    @DisplayName("특정 영화의 찜 된 횟수를 반환")
    void countMovie() {
        log.info("<- countMovie");

        // DB 에 새 유저 생성 & 반환
        List<UserEntity> testUserList = IntStream.range(0, TEST_SIZE).boxed()
                .map(i -> TESTING + i)
                .map(DibRepositoryTest::genUser)
                .map(t -> userRepo.saveNewUser(t))
                .toList();

        // 모든 유저가 testMovie 찜
        testUserList.forEach(t -> dibRepo.saveNewDib(t.getUserId(), testMovie.getMovieId()));

        // 유저 수 만큼 찜됨
        assertThat(dibRepo.countMovie(testMovie.getMovieId())).isEqualTo(testUserList.size());

        // 없을땐 0
        assertThat(dibRepo.countMovie("Random")).isEqualTo(0);

        log.info("-> countMovie");
    }


    @Test
    @DisplayName("특정 찜 목록을 삭제")
    void deleteDib() {
        log.info("<- deleteDib");

        DibEntity test = dibRepo.saveNewDib(testUser.getUserId(), testMovie.getMovieId());

        dibRepo.deleteDib(test.getDibId());

        assertThat(dibRepo.findByDibId(test.getDibId())).isEmpty();

        log.info("-> deleteDib");
    }

    @Test
    @DisplayName("특정 유저가 소유한 모든 찜 목록을 삭제")
    void testDeleteDib() {
        log.info("<- testDeleteDib");

        testMovieList.stream()
                .map(t -> movieRepo.saveNewMovie(t))
                .forEach(t -> dibRepo.saveNewDib(testUser.getUserId(), t.getMovieId()));

        dibRepo.deleteDib(testUser.getUserId());

        assertThat(dibRepo.findByUserId(testUser.getUserId())).isEmpty();

        log.info("-> testDeleteDib");
    }

    @Test
    @DisplayName("특정 유저의 특정 찜을 영화 ID 로 삭제")
    void testDeleteDib1() {
        log.info("<- testDeleteDib1");

        testMovieList.stream()
                .map(t -> movieRepo.saveNewMovie(t))
                .forEach(t -> dibRepo.saveNewDib(testUser.getUserId(), t.getMovieId()));

        dibRepo.saveNewDib(testUser.getUserId(), testMovie.getMovieId());

        dibRepo.deleteDib(testUser.getUserId(), testMovie.getMovieId());

        assertThat(dibRepo.findByUserId(testUser.getUserId()))
                .hasSize(testMovieList.size());

        log.info("-> testDeleteDib1");
    }
}