package core.application.reviews.services;

import static java.util.Comparator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import core.application.movies.models.entities.*;
import core.application.movies.repositories.movie.*;
import core.application.reviews.exceptions.*;
import core.application.reviews.models.entities.*;
import core.application.reviews.repositories.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.transaction.annotation.*;

@SpringBootTest
@Transactional
class ReviewServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImplTest.class);

    @Autowired
    ReviewService reviewService;

    @MockBean
    ReviewRepository reviewRepo;

    @MockBean
    CachedMovieRepository cachedMovieRepo;

    private static final int TEST_SIZE = 100;
    private static final String movieId = "test-12345";

    private static final Comparator<ReviewEntity> latest
            = comparing(ReviewEntity::getCreatedAt).reversed()
            .thenComparing(comparing(ReviewEntity::getMovieId).reversed());
    private static final Comparator<ReviewEntity> like
            = comparing(ReviewEntity::getLike).reversed()
            .thenComparing(comparing(ReviewEntity::getMovieId).reversed());

    private static List<ReviewEntity> testReviews;
    private static List<ReviewEntity> emptyTestReviews;

    private ReviewEntity genEntity(
            Long id, String movieId, String title, String content,
            int likes, Instant created, Instant updated) {
        return ReviewEntity.builder()
                .reviewId(id)
                .movieId(movieId)
                .title(title)
                .content(content)
                .like(likes)
                .createdAt(created)
                .updatedAt(updated)
                .build();
    }

    @FunctionalInterface
    private interface Triplet<T1, T2, T3, R> {

        R apply(T1 t1, T2 t2, T3 t3);
    }

    private void assertSortedSelection(
            Triplet<String, Integer, Integer, List<ReviewEntity>> triplet,
            Comparator<ReviewEntity> sortOrder,
            List<ReviewEntity> samples, String movieId) {
        when(triplet.apply(eq(movieId), anyInt(), anyInt())).thenAnswer(invocation -> {
            int offset = invocation.getArgument(1, Integer.class);
            int limit = invocation.getArgument(2, Integer.class);

            return samples.stream().sorted(sortOrder).skip(offset).limit(limit).toList();
        });
    }

    @BeforeEach
    void setUp() {
        when(cachedMovieRepo.findByMovieId(movieId))
                .thenReturn(Optional.of(
                        CachedMovieEntity.builder().build()
                ));

        Random random = new Random();
        testReviews = LongStream.range(0, TEST_SIZE)
                .boxed()
                .map(i -> genEntity(i, movieId, "Test review title", "Test content",
                        TEST_SIZE - i.intValue(), Instant.now(),
                        random.nextBoolean() ? Instant.now() : null))
                .toList();

        emptyTestReviews = testReviews.stream()
                .map(t -> genEntity(t.getReviewId(), t.getMovieId(), t.getTitle(),
                        null, t.getLike(), t.getCreatedAt(), t.getUpdatedAt()))
                .toList();

        testReviews.forEach(t -> when(reviewRepo.findByReviewId(t.getReviewId()))
                .thenReturn(Optional.of(t)));

        emptyTestReviews.forEach(
                t -> when(reviewRepo.findByReviewIdWithoutContent(t.getReviewId()))
                        .thenReturn(Optional.of(t)));

        assertSortedSelection(reviewRepo::findByMovieIdOnDateDescend, latest, testReviews, movieId);
        assertSortedSelection(reviewRepo::findByMovieIdOnLikeDescend, like, testReviews, movieId);

        assertSortedSelection(reviewRepo::findByMovieIdWithoutContentOnDateDescend, latest,
                emptyTestReviews, movieId);
        assertSortedSelection(reviewRepo::findByMovieIdWithoutContentOnLikeDescend, like,
                emptyTestReviews, movieId);
    }

    @Test
    @DisplayName("특정 영화에 달린 포스팅 목록을 확인.")
    void getReviewsOnMovieId() {

        int num = 5;
        for (int offset = 0; offset + num <= TEST_SIZE; offset += TEST_SIZE / 3) {

            for (ReviewSortOrder order : ReviewSortOrder.values()) {

                List<ReviewEntity> withContent = reviewService.getReviewsOnMovieId(movieId, order,
                        true, offset, num);
                List<ReviewEntity> withoutContent = reviewService.getReviewsOnMovieId(movieId,
                        order, false, offset, num);

                log.info("Check whether given list has correct size.");

                assertThat(withContent).hasSize(num);
                assertThat(withoutContent).hasSize(num);

                Comparator<ReviewEntity> correctOrder
                        = order == ReviewSortOrder.LATEST ? latest :
                        order == ReviewSortOrder.LIKE ? like : null;

                if (correctOrder == null) {
                    throw new RuntimeException();
                }

                List<ReviewEntity> correctAnswerWithContent
                        = testReviews.stream().sorted(correctOrder).toList();
                List<ReviewEntity> correctAnswerWithOutContent
                        = emptyTestReviews.stream().sorted(correctOrder).toList();

                log.info("Check whether given list maintains order.");
                assertThat(withContent).isSortedAccordingTo(correctOrder);
                assertThat(withoutContent).isSortedAccordingTo(correctOrder);

                log.info("Check whether correct subset were given.");
                assertThat(correctAnswerWithContent).containsAll(withContent);
                assertThat(correctAnswerWithOutContent).containsAll(withoutContent);

                log.info("Check whether given list's properties are valid.");
                withContent.forEach(r -> assertThat(r.getContent()).isNotNull());
                withoutContent.forEach(r -> assertThat(r.getContent()).isNull());

                log.info("LGTM!");
            }
        }
    }

    @Test
    @DisplayName("한 리뷰의 상세 정보를 가져오기.")
    void getReviewInfo() {

        log.info("Check whether method run correctly.");
        testReviews.forEach(t -> {
            ReviewEntity result = reviewService.getReviewInfo(t.getReviewId(), true);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(t);
        });

        emptyTestReviews.forEach(t -> {
            ReviewEntity result = reviewService.getReviewInfo(t.getReviewId(), false);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isNull();
            assertThat(result).isEqualTo(t);
        });

        Random random = new Random();

        log.info("Check whether exception were thrown with invalid argument.");
        assertThatThrownBy(() -> reviewService.getReviewInfo(random.nextLong(), true))
                .isInstanceOf(
                        NoReviewFoundException.class);

        log.info("good!");
    }

    @Test
    @DisplayName("포스팅 정보를 수정한다.")
    void updateReviewInfo() {

        testReviews.forEach(t ->
                when(reviewRepo.editReviewInfo(eq(t.getReviewId()),
                        any(ReviewEntity.class))).thenAnswer(invocation -> {
                    ReviewEntity arg = invocation.getArgument(1, ReviewEntity.class);

                    return genEntity(t.getReviewId(), t.getMovieId(), arg.getTitle(),
                            arg.getContent(), t.getLike(), t.getUpdatedAt(), Instant.now());
                })
        );

        ReviewEntity replacement = ReviewEntity.builder()
                .title("replacement")
                .content("replacement-content")
                .build();

        testReviews.forEach(t -> {
            ReviewEntity result = reviewService.updateReviewInfo(t.getReviewId(), replacement);

            assertThat(result).satisfies(
                    r -> assertThat(r.getTitle()).isEqualTo(replacement.getTitle()),
                    r -> assertThat(r.getContent()).isEqualTo(replacement.getContent()),
                    r -> assertThat(r.getReviewId()).isEqualTo(t.getReviewId()),
                    r -> assertThat(r.getMovieId()).isEqualTo(t.getMovieId())
            );
        });

        Random random = new Random();

        assertThatThrownBy(() -> reviewService.updateReviewInfo(random.nextLong(), replacement))
                .isInstanceOf(NoReviewFoundException.class);
    }

    @Test
    @DisplayName("포스팅을 삭제한다.")
    void deleteReview() {
        Random random = new Random();

        assertThatThrownBy(() -> reviewService.deleteReview(random.nextLong()))
                .isInstanceOf(NoReviewFoundException.class);
    }

    @Test
    @DisplayName("포스팅의 좋아요를 증감시킨다.")
    void testLikes() {
        Random random = new Random();

        assertThatThrownBy(() -> reviewService.increaseLikes(random.nextLong()))
                .isInstanceOf(NoReviewFoundException.class);

        assertThatThrownBy(() -> reviewService.decreaseLikes(random.nextLong()))
                .isInstanceOf(NoReviewFoundException.class);
    }
}