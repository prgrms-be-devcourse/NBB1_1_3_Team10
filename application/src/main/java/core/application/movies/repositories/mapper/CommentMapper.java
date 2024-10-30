package core.application.movies.repositories.mapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;

import core.application.movies.models.dto.response.CommentRespDTO;
import core.application.movies.models.entities.CommentEntity;

@Mapper
public interface CommentMapper {
	void save(String movieId, UUID userId, CommentEntity comment);

	Optional<CommentEntity> findByCommentId(Long commentId);

	Optional<CommentEntity> findByMovieIdAndUserId(String movieId, UUID userId);

	List<CommentRespDTO> findByMovieId(String movieId, UUID userId, int offset);

	List<CommentRespDTO> findByMovieIdOnDateDescend(String movieId, UUID userId, int offset);

	List<CommentRespDTO> findByMovieIdOnLikeDescend(String movieId, UUID userId, int offset);

	List<CommentRespDTO> findByMovieIdOnDislikeDescend(String movieId, UUID userId, int offset);

	List<CommentEntity> selectAll();

	int countByMovieId(String movieId);

	void update(CommentEntity comment);

	void delete(Long commentId);
}
