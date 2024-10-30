package core.application.reviews.repositories;

import static java.util.Comparator.*;
import static org.assertj.core.api.Assertions.*;

import core.application.movies.models.entities.*;
import core.application.movies.repositories.movie.*;
import core.application.reviews.models.entities.*;
import core.application.users.models.entities.*;
import core.application.users.repositories.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.transaction.annotation.*;

@SpringBootTest
@Transactional
class ReviewRepositoryTest {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CachedMovieRepository movieRepo;

    // 테스팅 용 user, movie
    private static final String TESTING = "TESTING TESTING";
    private static UserEntity testUser = UserEntity.builder()
            .userEmail(TESTING)
            .userName(TESTING)
            .role(UserRole.USER)
            .userPw(TESTING)
            .build();
    private static CachedMovieEntity testMovie = CachedMovieEntity.builder()
            .movieId(TESTING)
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

    /**
     * 테스팅 용 review 목록들 (페이징, 정렬 상태 확인용)
     */
    private static List<ReviewEntity> testReviews;
    private static final int TEST_SIZE = 100;

    /**
     * 손쉽게 review 엔티티 만들기
     */
    private static ReviewEntity genReview(UUID userId, String movieId, int like) {
        return ReviewEntity.builder()
                .title(TESTING)
                .content(TESTING)
                .like(like)
                .userId(userId)
                .movieId(movieId)
                .createdAt(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .build();
    }

    // 검사해야 할 order 들
    private final Comparator<ReviewEntity> dateDescend
            = comparing(ReviewEntity::getCreatedAt).reversed()
            .thenComparing(comparing(ReviewEntity::getReviewId).reversed());
    private final Comparator<ReviewEntity> likeDescend
            = comparing(ReviewEntity::getLike).reversed()
            .thenComparing(comparing(ReviewEntity::getReviewId).reversed());

    @BeforeEach
    void setUp() {
        // DB 저장된 거 있으면 가져오고 없으면 생성해서 가져옴
        testUser = userRepo.findByUserEmail(testUser.getUserEmail())
                .orElseGet(() -> {
                    userRepo.saveNewUser(testUser);
                    return userRepo.findByUserEmail(testUser.getUserEmail()).orElseThrow();
                });

        testMovie = movieRepo.findByMovieId(testMovie.getMovieId())
                .orElseGet(() -> movieRepo.saveNewMovie(testMovie));

        // testUser, testMovie 정보로 임의 리뷰 만듬
        testReviews = IntStream.range(0, TEST_SIZE).parallel()
                .boxed()
                .map(i -> genReview(testUser.getUserId(), testMovie.getMovieId(), TEST_SIZE - i))
                .toList();
    }

    @Test
    @DisplayName("새 리뷰 글을 만든다.")
    void saveNewReview() {
        log.info("<- saveNewReview");

        // 제대로 insert 할 엔티티 생성
        ReviewEntity testReview = genReview(testUser.getUserId(), testMovie.getMovieId(), 0);

        // DB 저장
        ReviewEntity result = reviewRepo.saveNewReview(testReview.getMovieId(),
                testReview.getUserId(), testReview);

        // 확인
        assertThat(result).satisfies(
                r -> assertThat(r).isNotNull(),
                r -> assertThat(r.getReviewId()).isNotNull(),
                r -> assertThat(r.getUserId()).isEqualTo(testUser.getUserId()).isNotNull(),
                r -> assertThat(r.getMovieId()).isEqualTo(testMovie.getMovieId()).isNotNull()
        );

        log.fine(result.toString());
        log.info("-> saveNewReview");
    }

    @Test
    @DisplayName("리뷰글 ID 로 찾는다.")
    void findByReviewId() {
        log.info("<- findByReviewId");

        ReviewEntity testReview = reviewRepo.saveNewReview(
                testMovie.getMovieId(), testUser.getUserId(),
                genReview(null, null, 0));

        ReviewEntity result = reviewRepo.findByReviewId(testReview.getReviewId()).orElseThrow();

        assertThat(testReview.equals(result)).isTrue();

        // 없을 땐 empty
        long random = new Random().nextLong();
        assertThat(reviewRepo.findByReviewId(random)).isEmpty();

        log.info("-> findByReviewId");
    }

    @Test
    @DisplayName("특정 영화에 달린 리뷰글을 보여준다.")
    void findByMovieId() {
        log.info("<- findByMovieId");

        // testReviews DB 에 저장, sorted 해서 변수로 가져옴
        List<ReviewEntity> testList = testReviews.stream()
                .map(t -> reviewRepo.saveNewReview(t.getMovieId(), t.getUserId(), t))
                .sorted(Comparator.comparing(ReviewEntity::getReviewId).reversed())
                .toList();

        testList.forEach(t -> log.fine(t.toString()));

        // DB 에서 페이징 된 거 가져옴
        List<ReviewEntity> result1 = reviewRepo.findByMovieId(testMovie.getMovieId(),
                0, TEST_SIZE / 2);

        // 원하는 개수 만큼, 내용 제대로 가져왔는지 확인
        assertThat(result1).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result1);

        result1.forEach(t -> log.fine(t.toString()));

        List<ReviewEntity> result2 = reviewRepo.findByMovieId(testMovie.getMovieId(),
                TEST_SIZE / 2, TEST_SIZE / 2);

        assertThat(result2).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result2);

        result2.forEach(t -> log.fine(t.toString()));

        // result1 이랑 result2 겹치는 거 없는지 확인
        assertThat(result1).doesNotContainAnyElementsOf(result2);

        // 없을 땐 empty
        String random = "RANDOM RANDOM";
        assertThat(reviewRepo.findByMovieId(random, 0, TEST_SIZE)).isEmpty();

        log.info("-> findByMovieId");
    }

    @Test
    @DisplayName("특정 영화에 달린 리뷰글을 최신순으로 보여준다.")
    void findByMovieIdOnDateDescend() {
        log.info("<- findByMovieIdOnDateDescend");

        // DB 에 리뷰글 저장
        List<ReviewEntity> testList = testReviews.stream()
                .map(t -> reviewRepo.saveNewReview(t.getMovieId(), t.getUserId(), t))
                .sorted(dateDescend)
                .toList();

        List<ReviewEntity> result1 = reviewRepo.findByMovieIdOnDateDescend(testMovie.getMovieId(),
                0, TEST_SIZE / 2);
        List<ReviewEntity> result2 = reviewRepo.findByMovieIdOnDateDescend(testMovie.getMovieId(),
                TEST_SIZE / 2, TEST_SIZE / 2);

        testList.forEach(t -> log.fine(t.toString()));
        result1.forEach(t -> log.fine(t.toString()));
        result2.forEach(t -> log.fine(t.toString()));

        // 가져온 개수, 내용, 정렬 잘 됐는지 확인
        assertThat(result1).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result1);
        assertThat(result1).isSortedAccordingTo(dateDescend);

        assertThat(result2).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result2);
        assertThat(result2).isSortedAccordingTo(dateDescend);

        // 겹치는 거 없는지 확인
        assertThat(result1).doesNotContainAnyElementsOf(result2);

        // 없을 땐 empty
        String random = "RANDOM RANDOM";
        assertThat(reviewRepo.findByMovieIdOnDateDescend(random, 0, TEST_SIZE)).isEmpty();

        log.info("-> findByMovieIdOnDateDescend");
    }

    @Test
    @DisplayName("특정 영화에 달린 리뷰글을 좋아요 순으로 보여준다.")
    void findByMovieIdOnLikeDescend() {
        log.info("<- findByMovieIdOnLikeDescend");

        // DB 에 리뷰들 저장
        List<ReviewEntity> testList = testReviews.stream()
                .map(t -> reviewRepo.saveNewReview(t.getMovieId(), t.getUserId(), t))
                .sorted(likeDescend)
                .toList();

        List<ReviewEntity> result1 = reviewRepo.findByMovieIdOnLikeDescend(testMovie.getMovieId(),
                0, TEST_SIZE / 2);
        List<ReviewEntity> result2 = reviewRepo.findByMovieIdOnLikeDescend(testMovie.getMovieId(),
                TEST_SIZE / 2, TEST_SIZE / 2);

        testList.forEach(t -> log.fine(t.toString()));
        result1.forEach(t -> log.fine(t.toString()));
        result2.forEach(t -> log.fine(t.toString()));

        // 가져온 개수, 내용, 정렬 잘 됐는지 확인
        assertThat(result1).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result1);
        assertThat(result1).isSortedAccordingTo(likeDescend);

        assertThat(result2).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result2);
        assertThat(result2).isSortedAccordingTo(likeDescend);

        // 겹치는 거 없는지 확인
        assertThat(result1).doesNotContainAnyElementsOf(result2);

        // 없을 땐 empty
        String random = "RANDOM RANDOM";
        assertThat(reviewRepo.findByMovieIdOnLikeDescend(random, 0, TEST_SIZE)).isEmpty();

        log.info("-> findByMovieIdOnLikeDescend");
    }

    @Test
    @DisplayName("특정 영화에 달린 리뷰글을 본문을 제외하고 불러온다.")
    void findByMovieIdWithoutContent() {
        log.info("<- findByMovieIdWithoutContent");

        // DB 에 리뷰들 저장
        List<ReviewEntity> testList = testReviews.stream()
                .map(t -> reviewRepo.saveNewReview(t.getMovieId(), t.getUserId(), t))
                .sorted(Comparator.comparing(ReviewEntity::getReviewId).reversed())
                .toList();

        List<ReviewEntity> result1 = reviewRepo.findByMovieIdWithoutContent(testMovie.getMovieId(),
                0, TEST_SIZE / 2);
        List<ReviewEntity> result2 = reviewRepo.findByMovieIdWithoutContent(testMovie.getMovieId(),
                TEST_SIZE / 2, TEST_SIZE / 2);

        testList.forEach(t -> log.fine(t.toString()));
        result1.forEach(t -> log.fine(t.toString()));
        result2.forEach(t -> log.fine(t.toString()));

        // 개수 잘 가져왓는지, 본문은 null 인지 확인
        assertThat(result1).hasSize(TEST_SIZE / 2);
        result1.forEach(r -> assertThat(r.getContent()).isNull());

        assertThat(result2).hasSize(TEST_SIZE / 2);
        result2.forEach(t -> assertThat(t.getContent()).isNull());

        // 없을 땐 empty
        String random = "RANDOM RANDOM";
        assertThat(reviewRepo.findByMovieIdWithoutContent(random, 0, TEST_SIZE)).isEmpty();

        log.info("-> findByMovieIdWithoutContent");
    }

    @Test
    @DisplayName("특정 리뷰 글을 수정한다.")
    void editReviewInfo() {
        log.info("<- editReviewInfo");

        // DB 에 글 저장
        ReviewEntity testReview = reviewRepo.saveNewReview(testMovie.getMovieId(),
                testUser.getUserId(), genReview(null, null, 0));

        ReviewEntity replacement = ReviewEntity.builder()
                .title("replacement")
                .content("replacement-content")
                .userId(UUID.randomUUID())
                .build();

        log.fine(testReview.toString());
        log.fine(replacement.toString());

        // DB 에 글 수정
        ReviewEntity result = reviewRepo.editReviewInfo(testReview.getReviewId(),
                replacement);

        log.fine(result.toString());

        // 불변값들 수정 안됬는지, 다른거 수정 됬는지 확인
        assertThat(result).satisfies(
                r -> assertThat(r.getReviewId()).isEqualTo(testReview.getReviewId()),
                r -> assertThat(r.getUserId()).isEqualTo(testUser.getUserId()),
                r -> assertThat(r.getMovieId()).isEqualTo(testMovie.getMovieId()),
                r -> assertThat(r.getCreatedAt()).isEqualTo(testReview.getCreatedAt()),
                r -> assertThat(r.getTitle()).isEqualTo(replacement.getTitle()),
                r -> assertThat(r.getContent()).isEqualTo(replacement.getContent()),
                r -> assertThat(r.getUpdatedAt()).isNotNull()
        );

        log.info("-> editReviewInfo");
    }

    @Test
    @DisplayName("특정 리뷰 글을 삭제한다.")
    void deleteReview() {
        log.info("<- deleteReview");

        // DB 에 글 저장
        ReviewEntity testReview = reviewRepo.saveNewReview(testMovie.getMovieId(),
                testUser.getUserId(), genReview(null, null, 0));

        // DB 에 진짜 저장됬는지 확인
        assertThat(reviewRepo.findByReviewId(testReview.getReviewId())).isNotEmpty();

        reviewRepo.deleteReview(testReview.getReviewId());

        // 삭제 됬는지 확인
        assertThat(reviewRepo.findByReviewId(testReview.getReviewId())).isEmpty();

        log.info("-> deleteReview");
    }

    @Test
    @DisplayName("특정 영화의 전체 포스팅 개수를 확인한다.")
    void countByMovieId() {
        log.info("<- countByMovieId");

        // DB 에 리뷰들 저장
        testReviews.forEach(t -> reviewRepo.saveNewReview(t.getMovieId(), t.getUserId(), t));

        Long result = reviewRepo.countByMovieId(testMovie.getMovieId());

        // 개수 확인
        assertThat(result).isEqualTo(TEST_SIZE);

        // 없으면 0
        assertThat(reviewRepo.countByMovieId("RANDOM RANDOM")).isEqualTo(0);

        log.info("-> countByMovieId");
    }
}