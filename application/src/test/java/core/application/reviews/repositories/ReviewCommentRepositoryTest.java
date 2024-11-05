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
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.transaction.annotation.*;

@SpringBootTest
@Transactional
class ReviewCommentRepositoryTest {

    private static final Logger log = Logger.getLogger(ReviewCommentRepositoryTest.class.getName());

    @Autowired
    private ReviewCommentRepository reviewCommentRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CachedMovieRepository movieRepo;

    private static final Random random = new Random();

    // 테스팅 용 user, movie, review
    private static final String TESTING = "TESTING TESTING";
    private static UserEntity testUser = new UserEntity(null, TESTING, TESTING, UserRole.USER, null, null, TESTING);

    private static CachedMovieEntity testMovie = new CachedMovieEntity(TESTING, TESTING, TESTING, TESTING, TESTING,
            TESTING, "1234", TESTING, TESTING, 0L, 0L, 0L, 0L);

    private static ReviewEntity testReview = new ReviewEntity(random.nextLong(Long.MAX_VALUE), TESTING, TESTING, null,
            null, 0, null, null);

    /**
     * 테스팅 용 comment 목록들 (페이징, 정렬 상태 확인용)
     */
    private static List<ReviewCommentEntity> testParent;
    private static List<ReviewCommentEntity> testChild;
    private static final int TEST_SIZE = 100;

    /**
     * 손쉽게 엔티티 만들기
     */
    private static ReviewCommentEntity genComment(UUID userId, Long reviewId,
            Long groupId, Long commentRef, int like) {
        return new ReviewCommentEntity(
                0L, reviewId, userId, TESTING, groupId, commentRef, 0, Instant.now().truncatedTo(ChronoUnit.SECONDS),
                false
        );
    }

    // 검사해야 할 order 들
    private final Comparator<ReviewCommentEntity> dateDescend
            = comparing(ReviewCommentEntity::getCreatedAt).reversed()
            .thenComparing(comparing(ReviewCommentEntity::getReviewCommentId).reversed());
    private final Comparator<ReviewCommentEntity> likeDescend
            = comparing(ReviewCommentEntity::getLike).reversed()
            .thenComparing(comparing(ReviewCommentEntity::getReviewCommentId).reversed());

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

        testReview = reviewRepo.findByReviewId(testReview.getReviewId())
                .orElseGet(() -> reviewRepo.saveNewReview(
                        testMovie.getMovieId(),
                        testUser.getUserId(),
                        testReview));

        // testParent, testChild 정보로 임의 댓글 만듬
        testParent = IntStream.range(0, TEST_SIZE).parallel()
                .boxed()
                .map(i -> genComment(testUser.getUserId(), testReview.getReviewId(),
                        null, null, TEST_SIZE - i))
                .toList();

        testChild = IntStream.range(0, TEST_SIZE).parallel()
                .boxed()
                .map(i -> genComment(testUser.getUserId(), testReview.getReviewId(),
                        null, null, TEST_SIZE - i))
                .toList();
    }

    @Test
    @DisplayName("새로운 부모 댓글 생성")
    void saveNewParentReviewComment() {
        log.info("<- saveNewParentReviewComment");

        Long randomGroup = new Random().nextLong();

        // insert 할 엔티티 생성
        ReviewCommentEntity testComment = genComment(null, null,
                randomGroup, null, 100);

        // DB 저장
        ReviewCommentEntity result = reviewCommentRepo.saveNewParentReviewComment(
                testReview.getReviewId(), testUser.getUserId(), testComment);

        // 확인
        assertThat(result).satisfies(
                r -> assertThat(r).isNotNull(),
                r -> assertThat(r.getReviewCommentId()).isNotNull(),
                r -> assertThat(r.getReviewId()).isEqualTo(testReview.getReviewId()).isNotNull(),
                r -> assertThat(r.getUserId()).isEqualTo(testUser.getUserId()).isNotNull(),
                r -> assertThat(r.getContent()).isEqualTo(TESTING),
                r -> assertThat(r.getGroupId()).isNull(),
                r -> assertThat(r.getLike()).isEqualTo(0),
                r -> assertThat(r.getCommentRef()).isNull(),
                r -> assertThat(r.getCreatedAt()).isNotNull(),
                r -> assertThat(r.isUpdated()).isFalse()
        );

        log.info("-> saveNewParentReviewComment");
    }

    @Test
    @DisplayName("새로운 자식 댓글 생성")
    void saveNewChildReviewComment() {
        log.info("<- saveNewChildReviewComment");

        long parentCommentId = reviewCommentRepo.saveNewParentReviewComment(
                testReview.getReviewId(), testUser.getUserId(),
                genComment(null, null, random.nextLong(), null, 0)
        ).getReviewCommentId();

        // insert 할 엔티티 생성
        ReviewCommentEntity testComment = genComment(null, testReview.getReviewId(),
                null, null, 100);

        // DB 저장
        ReviewCommentEntity result = reviewCommentRepo.saveNewChildReviewComment(parentCommentId,
                testUser.getUserId(), testComment);

        // 확인
        assertThat(result).satisfies(
                r -> assertThat(r).isNotNull(),
                r -> assertThat(r.getReviewCommentId()).isNotNull(),
                r -> assertThat(r.getReviewId()).isEqualTo(testReview.getReviewId()).isNotNull(),
                r -> assertThat(r.getUserId()).isEqualTo(testUser.getUserId()).isNotNull(),
                r -> assertThat(r.getContent()).isEqualTo(TESTING),
                r -> assertThat(r.getGroupId()).isEqualTo(parentCommentId).isNotNull(),
                r -> assertThat(r.getCommentRef()).isNull(),
                r -> assertThat(r.getCreatedAt()).isNotNull(),
                r -> assertThat(r.isUpdated()).isFalse()
        );

        log.info("-> saveNewChildReviewComment");
    }

    @Test
    @DisplayName("댓글 ID 로 검색")
    void findByReviewCommentId() {
        log.info("<- findByReviewCommentId");

        ReviewCommentEntity testComment = reviewCommentRepo.saveNewParentReviewComment(
                testReview.getReviewId(), testUser.getUserId(),
                genComment(null, null, null, null, 0)
        );

        ReviewCommentEntity result = reviewCommentRepo.findByReviewCommentId(
                testComment.getReviewCommentId()).orElseThrow();

        assertThat(result).isEqualTo(testComment);

        // 없을 땐 empty
        long random = new Random().nextLong();
        assertThat(reviewCommentRepo.findByReviewCommentId(random)).isEmpty();

        log.info("-> findByReviewCommentId");
    }

    @Test
    @DisplayName("어느 영화 리뷰에 달린 모든 부모 댓글을 검색")
    void findParentCommentByReviewId() {
        log.info("<- findParentCommentByReviewId");

        // testParent DB 에 저장, sorted 해서 변수로 가져옴.
        List<ReviewCommentEntity> testList = testParent.stream()
                .map(t -> reviewCommentRepo.saveNewParentReviewComment(
                        testReview.getReviewId(), testUser.getUserId(), t))
                .sorted(comparing(ReviewCommentEntity::getReviewCommentId).reversed())
                .toList();

        testList.forEach(t -> log.fine(t.toString()));

        // DB 에서 페이징 된 거 가져옴
        List<ReviewCommentEntity> result1 = reviewCommentRepo.findParentCommentByReviewId(
                testReview.getReviewId(), 0, TEST_SIZE / 2);

        result1.forEach(r -> log.fine(r.toString()));

        // 원하는 개수 만큼, 내용 제대로 가져왔는지 확인
        assertThat(result1).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result1);

        // 부모 댓글인지 확인
        result1.forEach(r -> assertThat(r.getGroupId()).isNull());

        List<ReviewCommentEntity> result2 = reviewCommentRepo.findParentCommentByReviewId(
                testReview.getReviewId(), TEST_SIZE / 2, TEST_SIZE / 2);

        result2.forEach(r -> log.fine(r.toString()));

        assertThat(result2).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result2);

        result2.forEach(r -> assertThat(r.getGroupId()).isNull());

        // result1 이랑 result2 겹치는 거 없는지 확인
        assertThat(result1).doesNotContainAnyElementsOf(result2);

        // 없을 땐 empty
        long random = new Random().nextLong();
        assertThat(reviewCommentRepo.findParentCommentByReviewId(random, 0, TEST_SIZE))
                .isEmpty();

        log.info("-> findParentCommentByReviewId");
    }

    @Test
    @DisplayName("어느 영화 리뷰의 모든 부모 댓글을 최신순으로 검색")
    void findParentCommentByReviewIdOnDateDescend() {
        log.info("<- findParentCommentByReviewIdOnDateDescend");

        // DB 에 댓글 저장, sorted 해서 변수로 가져옴.
        List<ReviewCommentEntity> testList = testParent.stream()
                .map(t -> reviewCommentRepo.saveNewParentReviewComment(
                        testReview.getReviewId(), testUser.getUserId(), t))
                .sorted(dateDescend)
                .toList();

        List<ReviewCommentEntity> result1 = reviewCommentRepo
                .findParentCommentByReviewIdOnDateDescend(
                        testReview.getReviewId(), 0, TEST_SIZE / 2);

        List<ReviewCommentEntity> result2 = reviewCommentRepo
                .findParentCommentByReviewIdOnDateDescend(
                        testReview.getReviewId(), TEST_SIZE / 2, TEST_SIZE / 2);

        testList.forEach(t -> log.fine(t.toString()));
        result1.forEach(t -> log.fine(t.toString()));
        result2.forEach(t -> log.fine(t.toString()));

        // 가져온 개수, 내용, 정렬 잘 됐는지 확인
        assertThat(result1).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result1);
        assertThat(result1).isSortedAccordingTo(dateDescend);

        // 부모 댓글인지 확인
        result1.forEach(r -> assertThat(r.getGroupId()).isNull());

        assertThat(result2).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result2);
        assertThat(result2).isSortedAccordingTo(dateDescend);

        result2.forEach(r -> assertThat(r.getGroupId()).isNull());

        // 겹치는 거 없는지 확인
        assertThat(result1).doesNotContainAnyElementsOf(result2);

        // 없을 땐 empty
        long random = new Random().nextLong();
        assertThat(reviewCommentRepo
                .findParentCommentByReviewIdOnDateDescend(random, 0, TEST_SIZE))
                .isEmpty();

        log.info("-> findParentCommentByReviewIdOnDateDescend");
    }

    @Test
    @DisplayName("어느 영화 리뷰의 모든 부모 댓글을 좋아요 순으로 검색")
    void findParentCommentByReviewIdOnLikeDescend() {
        log.info("<- findParentCommentByReviewIdOnLikeDescend");

        // DB 에 댓글 저장, sorted 해서 변수로 가져옴.
        List<ReviewCommentEntity> testList = testParent.stream()
                .map(t -> reviewCommentRepo.saveNewParentReviewComment(
                        testReview.getReviewId(), testUser.getUserId(), t))
                .sorted(likeDescend)
                .toList();

        List<ReviewCommentEntity> result1 = reviewCommentRepo
                .findParentCommentByReviewIdOnLikeDescend(
                        testReview.getReviewId(), 0, TEST_SIZE / 2);

        List<ReviewCommentEntity> result2 = reviewCommentRepo
                .findParentCommentByReviewIdOnLikeDescend(
                        testReview.getReviewId(), TEST_SIZE / 2, TEST_SIZE / 2);

        testList.forEach(t -> log.fine(t.toString()));
        result1.forEach(t -> log.fine(t.toString()));
        result2.forEach(t -> log.fine(t.toString()));

        // 가져온 개수, 내용, 정렬 잘 됐는지 확인
        assertThat(result1).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result1);
        assertThat(result1).isSortedAccordingTo(likeDescend);

        // 부모 댓글인지 확인
        result1.forEach(r -> assertThat(r.getGroupId()).isNull());

        assertThat(result2).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result2);
        assertThat(result2).isSortedAccordingTo(likeDescend);

        result2.forEach(r -> assertThat(r.getGroupId()).isNull());

        // 겹치는 거 없는지 확인
        assertThat(result1).doesNotContainAnyElementsOf(result2);

        // 없을 땐 empty
        long random = new Random().nextLong();
        assertThat(reviewCommentRepo
                .findParentCommentByReviewIdOnLikeDescend(random, 0, TEST_SIZE))
                .isEmpty();

        log.info("-> findParentCommentByReviewIdOnLikeDescend");
    }

    @Test
    @DisplayName("특정 부모 댓글의 모든 자식 댓글을 검색 (최신순 정렬 default)")
    void findChildCommentsByGroupId() {
        log.info("<- findChildCommentsByGroupId");

        // DB 에 댓글 저장
        Long testGroupId = reviewCommentRepo.saveNewParentReviewComment(
                        testReview.getReviewId(), testUser.getUserId(),
                        genComment(null, null, null, null, 0))
                .getReviewCommentId();

        List<ReviewCommentEntity> testList = testChild.stream()
                .map(t -> reviewCommentRepo.saveNewChildReviewComment(
                        testGroupId, testUser.getUserId(),
                        genComment(null, testReview.getReviewId(),
                                null, null, 0)
                )).toList();

        List<ReviewCommentEntity> result1 = reviewCommentRepo
                .findChildCommentsByGroupId(testGroupId, 0, TEST_SIZE / 2);

        List<ReviewCommentEntity> result2 = reviewCommentRepo
                .findChildCommentsByGroupId(testGroupId, TEST_SIZE / 2, TEST_SIZE / 2);

        testList.forEach(t -> log.fine(t.toString()));
        result1.forEach(t -> log.fine(t.toString()));
        result2.forEach(t -> log.fine(t.toString()));

        // 가져온 개수, 내용, 정렬 잘 됐는지 확인
        assertThat(result1).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result1);
        assertThat(result1).isSortedAccordingTo(dateDescend);

        // 올바른 자식 댓글인지 확인
        result1.forEach(r -> assertThat(r.getGroupId()).isEqualTo(testGroupId));

        assertThat(result2).hasSize(TEST_SIZE / 2);
        assertThat(testList).containsAll(result2);
        assertThat(result2).isSortedAccordingTo(dateDescend);

        result2.forEach(r -> assertThat(r.getGroupId()).isEqualTo(testGroupId));

        // 겹치는 거 없는지 확인
        assertThat(result1).doesNotContainAnyElementsOf(result2);

        // 없을 땐 empty
        long random = new Random().nextLong();
        assertThat(reviewCommentRepo
                .findParentCommentByReviewIdOnLikeDescend(random, 0, TEST_SIZE))
                .isEmpty();

        log.info("-> findChildCommentsByGroupId");
    }

    @Test
    @DisplayName("특정 댓글의 정보를 변경")
    void editReviewCommentInfo() {
        log.info("<- editReviewCommentInfo");

        Random random = new Random();

        // DB 에 글 저장
        ReviewCommentEntity testComment1 = reviewCommentRepo.saveNewParentReviewComment(
                testReview.getReviewId(), testUser.getUserId(),
                genComment(null, null, random.nextLong(), null, 0)
        );

        ReviewCommentEntity testComment2 = reviewCommentRepo.saveNewParentReviewComment(
                testReview.getReviewId(), testUser.getUserId(),
                genComment(null, null, random.nextLong(), null, 0)
        );

        Long randomCommentRef = random.nextLong();

        ReviewCommentEntity replacement = new ReviewCommentEntity(random.nextLong(), random.nextLong(),
                UUID.randomUUID(), "replacement-content", null,
                randomCommentRef, 0, null, false);

        log.fine(testComment1.toString());
        log.fine(testComment2.toString());
        log.fine(replacement.toString());

        // DB 에 글 수정
        ReviewCommentEntity result1
                = reviewCommentRepo.editReviewCommentInfo(
                testComment1.getReviewCommentId(), replacement, false
        ).orElseThrow();
        ReviewCommentEntity result2
                = reviewCommentRepo.editReviewCommentInfo(
                testComment2.getReviewCommentId(), replacement, true
        ).orElseThrow();

        log.fine(result1.toString());
        log.fine(result2.toString());

        // 기본 불변값들 확인
        BiConsumer<ReviewCommentEntity, ReviewCommentEntity> checkAssertion = (test, result) ->
                assertThat(result).satisfies(
                        r -> assertThat(r.getReviewCommentId())
                                .isEqualTo(test.getReviewCommentId()).isNotNull(),
                        r -> assertThat(r.getReviewId())
                                .isEqualTo(test.getReviewId()).isNotNull(),
                        r -> assertThat(r.getUserId())
                                .isEqualTo(test.getUserId()).isNotNull(),
                        r -> assertThat(r.getGroupId())
                                .isEqualTo(test.getGroupId()),
                        r -> assertThat(r.getLike())
                                .isEqualTo(test.getLike()),
                        r -> assertThat(r.getCreatedAt())
                                .isEqualTo(test.getCreatedAt()).isNotNull()
                );

        checkAssertion.accept(testComment1, result1);
        checkAssertion.accept(testComment2, result2);

        // 내용 변경 됐는지 확인
        assertThat(result1.getContent()).isEqualTo(replacement.getContent());
        assertThat(result2.getContent()).isEqualTo(replacement.getContent());

        assertThat(result1.getCommentRef()).isEqualTo(randomCommentRef);
        assertThat(result2.getCommentRef()).isEqualTo(randomCommentRef);

        assertThat(result1.isUpdated()).isFalse();
        assertThat(result2.isUpdated()).isTrue();

        // 없을 땐 에러
        long randomComment = random.nextLong();
        assertThatThrownBy(
                () -> reviewCommentRepo.editReviewCommentInfo(randomComment, replacement, true))
                .isInstanceOf(Exception.class);

        log.info("-> editReviewCommentInfo");
    }

    private interface TripleConsumer<T1, T2, T3> {

        void accept(T1 t1, T2 t2, T3 t3);
    }

    @Test
    @DisplayName("특정 포스팅 댓글의 좋아요를 수정")
    void updateReviewCommentLikes() {
        log.info("<- updateReviewCommentLikes");

        // DB 에 글 저장
        ReviewCommentEntity test = reviewCommentRepo.saveNewParentReviewComment(
                testReview.getReviewId(), testUser.getUserId(),
                genComment(null, null, null, null, 0)
        );

        log.fine(test.toString());

        int targetLikes = 100;

        // 좋아요 변경
        ReviewCommentEntity result = reviewCommentRepo.updateReviewCommentLikes(
                test.getReviewCommentId(), targetLikes).orElseThrow();

        log.fine(result.toString());

        TripleConsumer<ReviewCommentEntity, ReviewCommentEntity,
                Function<ReviewCommentEntity, Object>>
                checkEqualsAndNonNull = (t, r, func) -> assertThat(
                func.apply(r)).isEqualTo(func.apply(t)).isNotNull();

        // 기본 불변값들 확인
        checkEqualsAndNonNull.accept(test, result, ReviewCommentEntity::getReviewCommentId);
        checkEqualsAndNonNull.accept(test, result, ReviewCommentEntity::getReviewId);
        checkEqualsAndNonNull.accept(test, result, ReviewCommentEntity::getUserId);
        checkEqualsAndNonNull.accept(test, result, ReviewCommentEntity::getContent);
        checkEqualsAndNonNull.accept(test, result, ReviewCommentEntity::getCreatedAt);
        checkEqualsAndNonNull.accept(test, result, ReviewCommentEntity::isUpdated);

        // 좋아요 변경 됬는지 확인
        assertThat(result.getLike()).isEqualTo(targetLikes);

        // 없을 땐 에러
        long randomComment = new Random().nextLong();
        assertThatThrownBy(
                () -> reviewCommentRepo.updateReviewCommentLikes(randomComment, targetLikes))
                .isInstanceOf(Exception.class);

        log.info("-> updateReviewCommentLikes");
    }

    @Test
    @DisplayName("댓글의 개수를 파악")
    void countComments() {

        // 테스트용 부모 댓글 DB 저장
        testParent.forEach(t -> reviewCommentRepo.saveNewParentReviewComment(
                testReview.getReviewId(), testUser.getUserId(), t
        ));

        // 부모 댓글 수 확인
        assertThat(reviewCommentRepo
                .countParentCommentByReviewId(testReview.getReviewId()))
                .isEqualTo(testParent.size());

        // 테스트용 자식 댓글 DB 저장
        Long testGroupId = reviewCommentRepo.saveNewParentReviewComment(
                        testReview.getReviewId(), testUser.getUserId(),
                        genComment(null, null,
                                null, null, 0))
                .getReviewCommentId();

        testChild.forEach(t -> reviewCommentRepo.saveNewChildReviewComment(
                testGroupId, testUser.getUserId(),
                genComment(null, testReview.getReviewId(), null, null, 0)
        ));

        // 자식 댓글 수 확인
        assertThat(reviewCommentRepo
                .countChildCommentByGroupId(testGroupId))
                .isEqualTo(testChild.size());

        // 없을땐 0
        Random random = new Random();
        assertThat(reviewCommentRepo
                .countParentCommentByReviewId(random.nextLong()))
                .isEqualTo(0);

        assertThat(reviewCommentRepo
                .countChildCommentByGroupId(random.nextLong()))
                .isEqualTo(0);
    }
}
