package core.application.reviews.services;

import static java.util.Comparator.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import core.application.reviews.exceptions.*;
import core.application.reviews.models.entities.*;
import core.application.reviews.repositories.*;
import java.time.*;
import java.util.*;
import java.util.logging.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.transaction.annotation.*;

@SpringBootTest
@Transactional
class ReviewCommentServiceImplTest {

    private final Logger log = Logger.getLogger(ReviewCommentServiceImplTest.class.getName());

    @Autowired
    private ReviewCommentService reviewCommentService;

    @MockBean
    private ReviewCommentRepository reviewCommentRepo;

    @MockBean
    private ReviewRepository reviewRepo;

    private static final Random random = new Random();
    private static final int testSize = 100;

    private static final Comparator<ReviewCommentEntity> latestOrder = comparing(
            ReviewCommentEntity::getCreatedAt).reversed()
            .thenComparing(comparing(ReviewCommentEntity::getReviewCommentId).reversed());
    private static final Comparator<ReviewCommentEntity> mostLikeOrder = comparing(
            ReviewCommentEntity::getLike)
            .thenComparing(comparing(ReviewCommentEntity::getReviewCommentId).reversed());


    private ReviewCommentEntity genComment(Long reviewCommentId, Long reviewId, UUID userId,
                                           Long groupId, Long commentRef, Instant createdAt, boolean isUpdated) {
        return new ReviewCommentEntity(
                reviewCommentId, reviewId, userId, "", groupId, commentRef, 0, createdAt, isUpdated);
    }

    private List<ReviewCommentEntity> genComments(Long reviewId, Long groupId, Long commentRef) {
        return LongStream.range(0, testSize)
                .boxed()
                .map(i -> genComment(i, reviewId, UUID.randomUUID(),
                        groupId, commentRef, Instant.now(), false))
                .toList();
    }

    private List<ReviewCommentEntity> genParentComments(Long reviewId) {
        return genComments(reviewId, null, null);
    }

    private void setupRepo(List<Long> reviewId, List<Long> reviewCommentId) {
        when(reviewRepo.findByReviewId(anyLong())).thenAnswer(invocation -> {
            Long argReviewId = invocation.getArgument(0);
            return reviewId.contains(argReviewId) ? Optional.of(
                    new ReviewEntity(0L, "", null, null, null, 0, null, null)) : Optional.empty();
        });

        when(reviewCommentRepo.findByReviewCommentId(anyLong())).thenAnswer(invocation -> {
            Long argReviewCommentId = invocation.getArgument(0);
            return reviewCommentId.contains(argReviewCommentId) ? Optional.of(
                    new ReviewCommentEntity(0L, 0L, null, "", null, null, 0, null, false)) : Optional.empty();
        });

        when(reviewCommentRepo.updateReviewCommentLikes(anyLong(), anyInt()))
                .thenAnswer(invocation -> {
                    Long argReviewCommentId = invocation.getArgument(0);
                    return reviewCommentId.contains(argReviewCommentId) ? Optional.of(
                            new ReviewCommentEntity(0L, 0L, null, "", null, null, 0, null, false)) :
                            Optional.empty();
                });

        when(reviewCommentRepo.editReviewCommentInfo(anyLong(), any(ReviewCommentEntity.class), anyBoolean()))
                .thenAnswer(invocation -> {
                    Long argReviewCommentId = invocation.getArgument(0);
                    return reviewCommentId.contains(argReviewCommentId) ? Optional.of(
                            new ReviewCommentEntity(0L, 0L, null, "", null, null, 0, null, false)) : Optional.empty();
                });
    }

    @Test
    @DisplayName("특정 리뷰 포스팅의 부모 댓글을 불러오는 서비스")
    void getParentReviewComments() {
        // when
        log.info("<-- getParentReviewComments");

        Long reviewId = random.nextLong();
        ReviewEntity post = new ReviewEntity(0L, "", null, null, null, 0, null, null);

        List<ReviewCommentEntity> parentComments = genParentComments(reviewId);

        setupRepo(List.of(reviewId), List.of(random.nextLong()));

        parentComments.forEach(
                p -> when(reviewCommentRepo.findParentCommentByReviewIdOnDateDescend(reviewId, 0,
                        testSize))
                        .thenReturn(parentComments.stream().sorted(latestOrder).toList())
        );
        parentComments.forEach(
                p -> when(reviewCommentRepo.findParentCommentByReviewIdOnLikeDescend(reviewId, 0,
                        testSize))
                        .thenReturn(parentComments.stream().sorted(mostLikeOrder).toList())
        );

        // given
        List<ReviewCommentEntity> onLatest = reviewCommentService.getParentReviewComments(reviewId,
                ReviewCommentSortOrder.LATEST, 0, testSize);
        List<ReviewCommentEntity> onLikes = reviewCommentService.getParentReviewComments(reviewId,
                ReviewCommentSortOrder.LIKE, 0, testSize);

        // then
        assertThat(onLatest).containsAll(parentComments);
        assertThat(onLikes).containsAll(parentComments);

        assertThat(onLatest).isSortedAccordingTo(latestOrder);
        assertThat(onLikes).isSortedAccordingTo(mostLikeOrder);

        assertThatThrownBy(() -> reviewCommentService.getParentReviewComments(
                random.nextLong(), ReviewCommentSortOrder.LATEST, 0, testSize))
                .isInstanceOf(NoReviewFoundException.class);

        assertThatThrownBy(() -> reviewCommentService.getParentReviewComments(
                random.nextLong(), ReviewCommentSortOrder.LIKE, 0, testSize))
                .isInstanceOf(NoReviewFoundException.class);

        log.info("--> getParentReviewComments test passed");
    }

    @Test
    @DisplayName("특정 포스팅에 부모 댓글 다는 서비스")
    void addNewParentReviewComment() {
        log.info("<-- addNewParentReviewComment");

        Long reviewId = random.nextLong();
        ReviewCommentEntity temp = new ReviewCommentEntity(0L, null, null, "", null, null, 0, null, false);

        setupRepo(List.of(reviewId), List.of(random.nextLong()));

        assertThatThrownBy(() -> reviewCommentService.addNewParentReviewComment(
                random.nextLong(), UUID.randomUUID(), temp))
                .isInstanceOf(NoReviewFoundException.class);

        reviewCommentService.addNewParentReviewComment(reviewId, UUID.randomUUID(), temp);

        log.info("--> addNewParentReviewComment test passed");
    }

    @Test
    @DisplayName("특정 포스팅 내 부모 댓글에 자식 댓글 다는 서비스")
    void addNewChildReviewComment() {
        log.info("<-- addNewChildReviewComment");

        Long reviewId = random.nextLong();
        Long groupId = random.nextLong();

        ReviewCommentEntity temp = new ReviewCommentEntity(0L, null, null, "", null, null, 0, null, false);

        setupRepo(List.of(reviewId), List.of(groupId));

        assertThatThrownBy(() -> reviewCommentService.addNewChildReviewComment(
                random.nextLong(), groupId, UUID.randomUUID(), temp))
                .isInstanceOf(NoReviewFoundException.class);

        assertThatThrownBy(() -> reviewCommentService.addNewChildReviewComment(
                reviewId, random.nextLong(), UUID.randomUUID(), temp))
                .isInstanceOf(NoReviewCommentFoundException.class);

        reviewCommentService.addNewChildReviewComment(reviewId, groupId, UUID.randomUUID(), temp);

        log.info("--> addNewChildReviewComment test passed");
    }

    @Test
    @DisplayName("특정 댓글의 내용을 수정하는 서비스")
    void editReviewComment() {
        log.info("<-- editReviewComment");

        Long reviewCommentId = random.nextLong();
        Long refId = random.nextLong();

        String replacement = "this is replacement";

        setupRepo(List.of(random.nextLong()), List.of(reviewCommentId, refId));

        assertThatThrownBy(() -> reviewCommentService.editReviewComment(
                random.nextLong(), null, replacement))
                .isInstanceOf(NoReviewCommentFoundException.class);

        assertThatThrownBy(() -> reviewCommentService.editReviewComment(
                reviewCommentId, random.nextLong(), replacement))
                .isInstanceOf(NoReviewCommentFoundException.class);

        reviewCommentService.editReviewComment(reviewCommentId, refId, replacement);

        log.info("--> editReviewComment test passed");
    }

    @Test
    @DisplayName("특정 댓글을 삭제하는 서비스")
    void deleteReviewComment() {
        log.info("<-- deleteReviewComment");

        Long reviewCommentId = random.nextLong();
        setupRepo(List.of(random.nextLong()), List.of(reviewCommentId));

        assertThatThrownBy(() -> reviewCommentService.deleteReviewComment(random.nextLong()))
                .isInstanceOf(NoReviewCommentFoundException.class);

        reviewCommentService.deleteReviewComment(reviewCommentId);

        log.info("--> deleteReviewComment test passed");
    }

    @Test
    @DisplayName("특정 댓글의 좋아요를 1 증가시키는 서비스")
    void increaseCommentLike() {
        log.info("<-- increaseCommentLike");

        Long reviewCommentId = random.nextLong();

        System.out.println(reviewCommentId);
        setupRepo(List.of(random.nextLong()), List.of(reviewCommentId));

        assertThatThrownBy(() -> reviewCommentService.increaseCommentLike(random.nextLong()))
                .isInstanceOf(NoReviewCommentFoundException.class);

        reviewCommentService.increaseCommentLike(reviewCommentId);

        log.info("--> increaseCommentLike test passed");
    }

    @Test
    @DisplayName("특정 댓글의 좋아요를 1 감소시키는 서비스")
    void decreaseCommentLike() {
        log.info("<-- decreaseCommentLike");

        Long reviewCommentId = random.nextLong();

        setupRepo(List.of(random.nextLong()), List.of(reviewCommentId));

        assertThatThrownBy(() -> reviewCommentService.decreaseCommentLike(random.nextLong()))
                .isInstanceOf(NoReviewCommentFoundException.class);

        reviewCommentService.decreaseCommentLike(reviewCommentId);

        log.info("--> decreaseCommentLike test passed");
    }
}
