package core.application.movies.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import core.application.movies.models.dto.response.CommentRespDTO;
import core.application.movies.models.entities.CachedMovieEntity;
import core.application.movies.models.entities.CommentEntity;
import core.application.movies.repositories.comment.CommentLikeRepository;
import core.application.movies.repositories.comment.CommentRepository;
import core.application.movies.repositories.movie.CachedMovieRepository;
import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import core.application.users.repositories.UserRepository;

@SpringBootTest
@Transactional
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CachedMovieRepository movieRepository;
    private CommentEntity comment;
    private UUID userId;
    private String movieId;
    @Autowired
    private CommentLikeRepository commentLikeRepository;

	@BeforeEach
	public void setUp() {
        UserEntity user = new UserEntity(
                UUID.randomUUID(),
                "testEmail",
                "test",
                UserRole.USER,
                "nickname",
                "phone",
                "test"
        );
		userRepository.saveNewUser(user);
		userId = userRepository.findByUserEmail("testEmail").get().getUserId();

		CachedMovieEntity movieEntity = new CachedMovieEntity(
			"test",
			"testTitle",
			"posterUrl",
			"액션",
			"2024-09-30",
			"줄거리",
			"122",
			"마동석, 김무열",
			"봉준호",
			1L, 1L, 10L, 10L
		);
		movieRepository.saveNewMovie(movieEntity);
		movieId = movieRepository.findByMovieId(movieEntity.getMovieId()).get().getMovieId();

        String dateTime = "2024-10-31 07:57:23";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC); // UTC로 변환

        comment = new CommentEntity(
                1L,
                "test",
                1,
                1,
                10,
                "test",
                userRepository.findByUserEmail("testEmail").get().getUserId(),
                instant
        );
	}

    @Test
    @DisplayName("한줄평 리뷰를 저장한다.")
    public void save() {
        // GIVEN

        // WHEN
        CommentEntity save = commentRepository.saveNewComment("test", comment.getUserId(), comment);

        // THEN
        assertThat(save).isNotNull();
        assertThat(save.getContent()).isEqualTo(comment.getContent());
        assertThat(save.getLike()).isEqualTo(comment.getLike());
        assertThat(save.getDislike()).isEqualTo(comment.getDislike());
        assertThat(save.getRating()).isEqualTo(comment.getRating());
        assertThat(save.getMovieId()).isEqualTo(comment.getMovieId());
        assertThat(save.getUserId()).isEqualTo(comment.getUserId());
    }

    @Test
    @DisplayName("로그인하지 않은 사용자가 영화 ID로 조회한다.")
    public void findByMovieId() {
        // GIVEN
        CommentEntity save1 = commentRepository.saveNewComment(comment.getMovieId(), comment.getUserId(),
                comment);

        String dateTime = "2024-10-31 07:57:23";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC); // UTC로 변환

        CommentEntity comment2 = new CommentEntity(
                1L,
                "내용2",
                1,
                1,
                10,
                "test",
                userId,
                instant
        );
        CommentEntity save2 = commentRepository.saveNewComment(comment2.getMovieId(), comment2.getUserId(), comment2);

        // WHEN
        List<CommentRespDTO> finds = commentRepository.findByMovieId(comment.getMovieId(), null, 0).getContent();

        // THEN
        assertThat(finds.size()).isEqualTo(2);

        for (CommentRespDTO find : finds) {
            assertThat(find.getMovieId()).isEqualTo("test");
        }
    }

//    @Test
//    @DisplayName("로그인한 사용자가 한줄평 조회 시, 좋아요한 한줄평을 구분한다.")
//    public void findByMovieIdWithUser() {
//
//        // GIVEN
//        CommentEntity save1 = commentRepository.saveNewComment(comment.getMovieId(), comment.getUserId(),
//                comment);
//        commentLikeRepository.saveCommentLike(14L, userId); //쿼리 문제 발생
//
//        // WHEN
//        List<CommentRespDTO> finds = commentRepository.findByMovieId(comment.getMovieId(), userId, 0).getContent();
//
//        // THEN
//        assertThat(finds.size()).isEqualTo(1);
//
//        assertThat(finds.get(0).isLiked()).isTrue();
//    }

    @Test
    @DisplayName("특정 영화의 한줄평을 시간순으로 조회한다.")
    public void findByMovieIdOnDateDescend() throws InterruptedException {
        // GIVEN

        String dateTime = "2024-10-31 07:57:23";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC); // UTC로 변환

        CommentEntity comment2 = new CommentEntity(
                1L,
                "내용2",
                1,
                1,
                10,
                "test",
                userRepository.findByUserEmail("testEmail").get().getUserId(),
                instant
        );
        commentRepository.saveNewComment(comment.getMovieId(), comment.getUserId(), comment);
        Thread.sleep(1000);
        commentRepository.saveNewComment(comment2.getMovieId(), comment2.getUserId(), comment2);

        // WHEN
        List<CommentRespDTO> finds = commentRepository.findByMovieIdOnDateDescend(comment.getMovieId(), userId, 0)
                .getContent();

        // THEN
        Instant later = finds.get(0).getCreatedAt();
        for (CommentRespDTO find : finds) {
            assertThat(later).isAfterOrEqualTo(find.getCreatedAt());
            later = find.getCreatedAt();
        }
    }

    @Test
    @DisplayName("특정 영화 한줄평을 좋아요 순으로 검색한다.")
    public void testLikeOrder() {
        // GIVEN

        String dateTime = "2024-10-31 07:57:23";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC); // UTC로 변환

        CommentEntity comment2 = new CommentEntity(
                1L,
                "내용2",
                100,
                1,
                10,
                "test",
                userRepository.findByUserEmail("testEmail").get().getUserId(),
                instant
        );
        commentRepository.saveNewComment(comment.getMovieId(), comment.getUserId(), comment);
        commentRepository.saveNewComment(comment2.getMovieId(), comment2.getUserId(), comment2);

        // WHEN
        List<CommentRespDTO> finds = commentRepository.findByMovieIdOnLikeDescend(comment.getMovieId(), userId, 0).getContent();

        // THEN
        int more = finds.get(0).getLike();
        for (CommentRespDTO find : finds) {
            assertThat(more).isGreaterThanOrEqualTo(find.getLike());
            more = find.getLike();
        }
    }

    @Test
    @DisplayName("특정 영화 한줄평을 싫어요 순으로 검색한다.")
    public void testDislikeOrder() {
        // GIVEN
        String dateTime = "2024-10-31 07:57:23";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        Instant instant = localDateTime.toInstant(ZoneOffset.UTC); // UTC로 변환

        CommentEntity comment2 = new CommentEntity(
                1L,
                "내용2",
                100,
                1,
                10,
                "test",
                userRepository.findByUserEmail("testEmail").get().getUserId(),
                instant
        );
        commentRepository.saveNewComment(comment.getMovieId(), comment.getUserId(), comment);
        commentRepository.saveNewComment(comment2.getMovieId(), comment2.getUserId(), comment2);

        // WHEN
        List<CommentRespDTO> finds = commentRepository.findByMovieIdOnDislikeDescend(comment.getMovieId(), userId, 0).getContent();

        // THEN
        int more = finds.get(0).getDislike();
        for (CommentRespDTO find : finds) {
            assertThat(more).isGreaterThanOrEqualTo(find.getDislike());
            more = find.getDislike();
        }
    }

    @Test
    @DisplayName("한줄평을 삭제한다.")
    public void delete() {
        // GIVEN
        commentRepository.saveNewComment(comment.getMovieId(), comment.getUserId(), comment);

        // WHEN
        commentRepository.deleteComment(comment.getCommentId());

        // THEN
        Optional<CommentEntity> find = commentRepository.findByCommentId(comment.getCommentId());
        assertThat(find).isEmpty();
    }
}
