package core.application.movies.service;

import static org.assertj.core.api.Assertions.*;

import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import core.application.movies.constant.CommentSort;
import core.application.movies.models.dto.request.CommentWriteReqDTO;
import core.application.movies.models.dto.response.CommentRespDTO;
import core.application.movies.models.entities.CachedMovieEntity;
import core.application.movies.models.entities.CommentEntity;
import core.application.movies.repositories.comment.CommentRepository;
import core.application.movies.repositories.movie.CachedMovieRepository;
import core.application.users.models.entities.UserEntity;
import core.application.users.models.entities.UserRole;
import core.application.users.repositories.UserRepository;

@SpringBootTest
@Transactional
public class CommentServiceTest {

	@Autowired
	private CommentService commentService;
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CachedMovieRepository movieRepository;
	private List<UserEntity> users = new ArrayList<>();
	private String movieId;

	@BeforeEach
	public void setUp() {
		for (int i = 0; i < 10; i++) {
            UserEntity testUser = new UserEntity(
                    UUID.randomUUID(),
                    String.valueOf(i),
                    "test",
                    UserRole.USER,
                    "nickname",
                    "phone",
                    "test"
            );
			userRepository.saveNewUser(testUser);
			users.add(userRepository.findByUserEmail(String.valueOf(i)).orElseThrow());
		}

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
		CachedMovieEntity save = movieRepository.saveNewMovie(movieEntity);
		movieId = save.getMovieId();
	}

	@Test
	@DisplayName("한줄평을 작성한다.")
	public void writeComment() {
		// GIVEN
		CommentWriteReqDTO writeReqDTO = new CommentWriteReqDTO("한줄평 내용입니다.", 10);
		UserEntity writer = users.get(0);

		// WHEN
		CommentRespDTO save = commentService.writeCommentOnMovie(writeReqDTO, writer, movieId);

		// THEN
		Optional<CommentEntity> find = commentRepository.findByCommentId(save.getCommentId());
		assertThat(find).isPresent();
		assertThat(find.get().getContent()).isEqualTo(writeReqDTO.getContent());
		assertThat(find.get().getRating()).isEqualTo(writeReqDTO.getRating());
	}

	@Test
	@DisplayName("영화의 한줄평을 최신순으로 불러온다.")
	public void getLatestComments() throws InterruptedException {
		// GIVEN
		for (int i = 0; i < 10; i++) {
			CommentWriteReqDTO writeReqDTO = new CommentWriteReqDTO(i + "번째 한줄평", 10);
			CommentRespDTO commentRespDTO = commentService.writeCommentOnMovie(writeReqDTO, users.get(i),
				movieId);
		}

		// WHEN
		List<CommentRespDTO> comments = commentService.getComments(movieId, 0, CommentSort.LATEST, null).getContent();

		// THEN
		assertThat(comments.size()).isEqualTo(10);
		Instant later = comments.get(0).getCreatedAt();
		for (int i = 1; i < comments.size(); i++) {
			assertThat(later).isAfterOrEqualTo(comments.get(i).getCreatedAt());
			later = comments.get(i).getCreatedAt();
		}
	}

	@Test
	@DisplayName("한줄평을 좋아요 순으로 불러온다.")
	public void getMostLikedComments() {
		// GIVEN
		for (int i = 0; i < 10; i++) {
			CommentWriteReqDTO writeReqDTO = new CommentWriteReqDTO(i + "번째 한줄평", 10);
			CommentRespDTO commentRespDTO = commentService.writeCommentOnMovie(writeReqDTO, users.get(i),
				movieId);
			for (int j = 0; j < i; j++) {
				UserEntity user = users.get(j);
				commentService.incrementCommentLike(commentRespDTO.getCommentId(), user.getUserId());
			}
		}

		// WHEN
		List<CommentRespDTO> comments = commentService.getComments(movieId, 0, CommentSort.LIKE, null).getContent();

		// THEN
		int like = comments.get(0).getLike();
		for (int i = 1; i < comments.size(); i++) {
			assertThat(like).isGreaterThanOrEqualTo(comments.get(i).getLike());
			like = comments.get(i).getLike();
		}
	}

	@Test
	@DisplayName("한줄평을 싫어요 순으로 불러온다.")
	public void getMostDislikedComments() {
		// GIVEN
		for (int i = 0; i < 10; i++) {
			CommentWriteReqDTO writeReqDTO = new CommentWriteReqDTO(i + "번째 한줄평", 10);
			CommentRespDTO commentRespDTO = commentService.writeCommentOnMovie(writeReqDTO, users.get(i),
				movieId);
			for (int j = 0; j < i; j++) {
				UserEntity user = users.get(j);
				commentService.incrementCommentDislike(commentRespDTO.getCommentId(), user.getUserId());
			}
		}

		// WHEN
		List<CommentRespDTO> comments = commentService.getComments(movieId, 0, CommentSort.DISLIKE, null).getContent();

		// THEN
		int dislike = comments.get(0).getDislike();
		for (int i = 1; i < comments.size(); i++) {
			assertThat(dislike).isGreaterThanOrEqualTo(comments.get(i).getDislike());
			dislike = comments.get(i).getDislike();
		}
	}

	@Test
	@DisplayName("한줄평에 좋아요을 누른다.")
	public void likeComment() {
		// GIVEN
		CommentWriteReqDTO writeReqDTO = new CommentWriteReqDTO("한줄평입니다.", 10);
		UserEntity writer = users.get(0);
		CommentRespDTO commentRespDTO = commentService.writeCommentOnMovie(writeReqDTO, writer, movieId);

		// WHEN
		commentService.incrementCommentLike(commentRespDTO.getCommentId(), writer.getUserId());

		// THEN
		CommentEntity comment = commentRepository.findByCommentId(commentRespDTO.getCommentId())
			.orElseThrow(() -> new RuntimeException("존재하지 않은 한줄평입니다."));
		assertThat(comment.getLike()).isEqualTo(1);
	}

	@Test
	@DisplayName("좋아요를 취소한다.")
	public void cancelLikeComment() {
		// GIVEN
		CommentWriteReqDTO writeReqDTO = new CommentWriteReqDTO("한줄평입니다.", 10);
		UserEntity writer = users.get(0);
		CommentRespDTO commentRespDTO = commentService.writeCommentOnMovie(writeReqDTO, writer,
			movieId);
		commentService.incrementCommentLike(commentRespDTO.getCommentId(), users.get(1).getUserId());

		// WHEN
		commentService.decrementCommentLike(commentRespDTO.getCommentId(), users.get(1).getUserId());

		// THEN
		CommentEntity comment = commentRepository.findByCommentId(commentRespDTO.getCommentId()).orElseThrow();
		assertThat(comment.getLike()).isEqualTo(0);
	}

	@Test
	@DisplayName("한줄평에 싫어요를 누른다.")
	public void dislikeComment() {
		// GIVEN
		CommentWriteReqDTO writeReqDTO = new CommentWriteReqDTO("한줄평입니다.", 10);
		UserEntity writer = users.get(0);
		CommentRespDTO commentRespDTO = commentService.writeCommentOnMovie(writeReqDTO, writer, movieId);

		// WHEN
		commentService.incrementCommentDislike(commentRespDTO.getCommentId(), writer.getUserId());

		// THEN
		CommentEntity comment = commentRepository.findByCommentId(commentRespDTO.getCommentId())
			.orElseThrow(() -> new RuntimeException("존재하지 않은 한줄평입니다."));
		assertThat(comment.getDislike()).isEqualTo(1);
	}

	@Test
	@DisplayName("싫어요를 취소한다.")
	public void cancelDislikeComment() {
		// GIVEN
		CommentWriteReqDTO writeReqDTO = new CommentWriteReqDTO("한줄평입니다.", 10);
		UserEntity writer = users.get(0);
		CommentRespDTO commentRespDTO = commentService.writeCommentOnMovie(writeReqDTO, writer,
			movieId);
		commentService.incrementCommentDislike(commentRespDTO.getCommentId(), users.get(1).getUserId());

		// WHEN
		commentService.decrementCommentDislike(commentRespDTO.getCommentId(), users.get(1).getUserId());

		// THEN
		CommentEntity comment = commentRepository.findByCommentId(commentRespDTO.getCommentId()).orElseThrow();
		assertThat(comment.getDislike()).isEqualTo(0);
	}

	@Test
	@DisplayName("한줄평 조회 시, 좋아요와 싫어요를 누른 항목은 표시된다.")
	public void displayTest() {
		// GIVEN
		Set<Long> reactionCommentIds = new HashSet<>();
		UserEntity user = users.get(0);
		for (int i = 0; i < 10; i++) {
			CommentWriteReqDTO writeReqDTO = new CommentWriteReqDTO(i + "번째 한줄평", 10);
			CommentRespDTO commentRespDTO = commentService.writeCommentOnMovie(writeReqDTO, users.get(i),
				movieId);
			if (i < 5) {
				commentService.incrementCommentLike(commentRespDTO.getCommentId(), user.getUserId());
				commentService.incrementCommentDislike(commentRespDTO.getCommentId(), user.getUserId());
				reactionCommentIds.add(commentRespDTO.getCommentId());
			}
		}

		// WHEN
		List<CommentRespDTO> comments = commentService.getComments(movieId, 0, CommentSort.LIKE, user.getUserId())
			.getContent();

		// THEN
		for (CommentRespDTO comment : comments) {
			if (reactionCommentIds.contains(comment.getCommentId())) {
				assertThat(comment.isLiked()).isTrue();
				assertThat(comment.isDisliked()).isTrue();
			} else {
				assertThat(comment.isLiked()).isFalse();
				assertThat(comment.isDisliked()).isFalse();
			}
		}
	}
}
