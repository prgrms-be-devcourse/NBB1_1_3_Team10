package core.application.movies.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import core.application.movies.constant.CommentSort;
import core.application.movies.exception.InvalidReactionException;
import core.application.movies.exception.InvalidWriteCommentException;
import core.application.movies.exception.NoMovieException;
import core.application.movies.exception.NotCommentWriterException;
import core.application.movies.exception.NotFoundCommentException;
import core.application.movies.exception.NotMatchMovieCommentException;
import core.application.movies.models.dto.request.CommentWriteReqDTO;
import core.application.movies.models.dto.response.CommentRespDTO;
import core.application.movies.models.entities.CachedMovieEntity;
import core.application.movies.models.entities.CommentEntity;
import core.application.movies.repositories.comment.CommentDislikeRepository;
import core.application.movies.repositories.comment.CommentLikeRepository;
import core.application.movies.repositories.comment.CommentRepository;
import core.application.movies.repositories.movie.CachedMovieRepository;
import core.application.users.models.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
	private final CachedMovieRepository movieRepository;
	private final CommentRepository commentRepository;
	private final CommentLikeRepository likeRepository;
	private final CommentDislikeRepository dislikeRepository;

	@Transactional(readOnly = true)
	public Page<CommentRespDTO> getComments(String movieId, int page, CommentSort sort, UUID userId) {
		return switch (sort) {
			case LIKE -> commentRepository.findByMovieIdOnLikeDescend(movieId, userId, page);
			case LATEST -> commentRepository.findByMovieIdOnDateDescend(movieId, userId, page);
			default -> commentRepository.findByMovieIdOnDislikeDescend(movieId, userId, page);
		};
	}

	@Transactional
	public CommentRespDTO writeCommentOnMovie(CommentWriteReqDTO writeReqDTO, UserEntity user, String movieId) {
		// 이미 작성한 기록이 있는지 확인한다.
		log.info("user = {}", user);
		if (commentRepository.existsByMovieIdAndUserId(movieId, user.getUserId())) {
			throw new InvalidWriteCommentException("한줄평은 1회 작성만 가능합니다.");
		}
		CommentEntity newComment = CommentEntity.of(writeReqDTO, movieId, user.getUserId());
		CommentEntity save = commentRepository.saveNewComment(movieId, user.getUserId(), newComment);
		CachedMovieEntity movie = movieRepository.findByMovieId(movieId)
			.orElseThrow(() -> new NoMovieException("존재하지 않는 영화입니다."));
		log.info("수정 전 영화 총 평점 : {}, 수정 전 영화 한줄평 개수 : {}", movie.getSumOfRating(), movie.getCommentCount());
		movie.isCommentedWithRating(newComment.getRating());
		log.info("수정된 영화 총 평점 : {}, 수정된 영화 한줄평 개수 : {}", movie.getSumOfRating(), movie.getCommentCount());
		movieRepository.editMovie(movieId, movie);
		return CommentRespDTO.of(save, user.getAlias());
	}

	@Transactional
	public void deleteCommentOnMovie(String movieId, UUID userId, Long commentId) {
		CommentEntity comment = commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new NotFoundCommentException("존재하지 않는 한줄평입니다."));
		if (!comment.getUserId().equals(userId)) {
			throw new NotCommentWriterException("한줄평 작성자가 아닙니다.");
		}
		if (!comment.getMovieId().equals(movieId)) {
			throw new NotMatchMovieCommentException("해당 영화의 한줄평이 아닙니다.");
		}

		commentRepository.deleteComment(commentId);
		CachedMovieEntity movie = movieRepository.findByMovieId(movieId)
			.orElseThrow(() -> new NoMovieException("존재하는 영화가 아닙니다."));
		log.info("[MovieService.deleteCommentOnMovie] 영화 정보 수정");
		log.info("[MovieService.deleteCommentOnMovie] before rating : {}, commentCount : {}", movie.getSumOfRating(),
			movie.getCommentCount());
		movie.deleteComment(comment.getRating());
		log.info("[MovieService.deleteCommentOnMovie] before rating : {}, commentCount : {}", movie.getSumOfRating(),
			movie.getCommentCount());
		movieRepository.editMovie(movieId, movie);
	}

	@Transactional
	public void incrementCommentLike(Long commentId, UUID userId) {
		CommentEntity comment = commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new NotFoundCommentException("존재하지 않는 한줄평입니다."));
		if (likeRepository.isExistLike(commentId, userId)) {
			throw new InvalidReactionException("이미 '좋아요'를 누른 한줄평입니다.");
		}
		comment.isLiked();
		commentRepository.update(comment);
		likeRepository.saveCommentLike(commentId, userId);
	}

	@Transactional
	public void decrementCommentLike(Long commentId, UUID userId) {
		CommentEntity comment = commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new NotFoundCommentException("존재하지 않는 한줄평입니다."));
		if (!likeRepository.isExistLike(commentId, userId)) {
			throw new InvalidReactionException("'좋아요'를 누르지 않은 한줄평입니다.");
		}
		comment.cancelLike();
		commentRepository.update(comment);
		likeRepository.deleteCommentLike(commentId, userId);
	}

	@Transactional
	public void incrementCommentDislike(Long commentId, UUID userId) {
		CommentEntity comment = commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new NotFoundCommentException("존재하지 않는 한줄평입니다."));
		if (dislikeRepository.isExistDislike(commentId, userId)) {
			throw new InvalidReactionException("이미 '싫어요'를 누른 한줄평입니다.");
		}
		comment.isDisliked();
		commentRepository.update(comment);
		dislikeRepository.saveCommentDislike(commentId, userId);
	}

	@Transactional
	public void decrementCommentDislike(Long commentId, UUID userId) {
		CommentEntity comment = commentRepository.findByCommentId(commentId)
			.orElseThrow(() -> new NotFoundCommentException("존재하지 않는 한줄평입니다."));
		if (!dislikeRepository.isExistDislike(commentId, userId)) {
			throw new InvalidReactionException("'싫어요'를 누르지 않은 한줄평입니다.");
		}
		comment.cancelDislike();
		commentRepository.update(comment);
		dislikeRepository.deleteCommentDislike(commentId, userId);
	}
}
