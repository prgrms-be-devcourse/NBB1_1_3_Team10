package core.application.movies.repositories.comment.mybatis;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import core.application.movies.models.dto.response.CommentRespDTO;
import core.application.movies.models.entities.CommentEntity;
import core.application.movies.repositories.comment.CommentRepository;
import core.application.movies.repositories.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Profile("mybatis")
public class MybatisCommentRepository implements CommentRepository {

	private final CommentMapper commentMapper;

	@Override
	public CommentEntity saveNewComment(String movieId, UUID userId, CommentEntity comment) {
		commentMapper.save(movieId, userId, comment);
		return findByCommentId(comment.getCommentId()).orElse(null);
	}

	@Override
	public Optional<CommentEntity> findByCommentId(Long commentId) {
		return commentMapper.findByCommentId(commentId);
	}

	@Override
	public Boolean existsByMovieIdAndUserId(String movieId, UUID userId) {
		return commentMapper.findByMovieIdAndUserId(movieId, userId).isPresent();
	}

	@Override
	public Page<CommentRespDTO> findByMovieId(String movieId, UUID userId, int page) {
		Pageable pageable = PageRequest.of(page, 10);
		int total = commentMapper.countByMovieId(movieId);
		List<CommentRespDTO> find = commentMapper.findByMovieId(movieId, userId, page * 10);
		return new PageImpl<>(find, pageable, total);
	}

	@Override
	public Page<CommentRespDTO> findByMovieIdOnDateDescend(String movieId, UUID userId, int page) {
		Pageable pageable = PageRequest.of(page, 10);
		int total = commentMapper.countByMovieId(movieId);
		List<CommentRespDTO> find = commentMapper.findByMovieIdOnDateDescend(movieId, userId, page * 10);
		return new PageImpl<>(find, pageable, total);
	}

	@Override
	public Page<CommentRespDTO> findByMovieIdOnLikeDescend(String movieId, UUID userId, int page) {
		Pageable pageable = PageRequest.of(page, 10);
		int total = commentMapper.countByMovieId(movieId);
		List<CommentRespDTO> find = commentMapper.findByMovieIdOnLikeDescend(movieId, userId, page * 10);
		return new PageImpl<>(find, pageable, total);
	}

	@Override
	public Page<CommentRespDTO> findByMovieIdOnDislikeDescend(String movieId, UUID userId, int page) {
		Pageable pageable = PageRequest.of(page, 10);
		int total = commentMapper.countByMovieId(movieId);
		List<CommentRespDTO> find = commentMapper.findByMovieIdOnDislikeDescend(movieId, userId,
			page * 10);
		return new PageImpl<>(find, pageable, total);
	}

	@Override
	public List<CommentEntity> selectAll() {
		return commentMapper.selectAll();
	}

	@Override
	public void update(CommentEntity comment) {
		commentMapper.update(comment);
	}

	@Override
	public void deleteComment(Long commentId) {
		commentMapper.delete(commentId);
	}
}
