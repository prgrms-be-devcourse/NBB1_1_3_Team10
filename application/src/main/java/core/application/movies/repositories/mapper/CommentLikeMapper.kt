package core.application.movies.repositories.mapper;

import java.util.UUID;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentLikeMapper {
	void save(Long commentId, UUID userId);

	int countLikeByUser(Long commentId, UUID userId);

	void delete(Long commentId, UUID userId);
}
