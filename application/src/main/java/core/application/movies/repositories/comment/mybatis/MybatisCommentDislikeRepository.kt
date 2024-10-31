package core.application.movies.repositories.comment.mybatis;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import core.application.movies.repositories.comment.CommentDislikeRepository;
import core.application.movies.repositories.mapper.CommentDislikeMapper;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Profile("mybatis")
public class MybatisCommentDislikeRepository implements CommentDislikeRepository {
	private final CommentDislikeMapper commentDislikeMapper;

	public void saveCommentDislike(Long commentId, UUID userId) {
		commentDislikeMapper.save(commentId, userId);
	}

	public boolean isExistDislike(Long commentId, UUID userId) {
		int count = commentDislikeMapper.countLikeByUser(commentId, userId);
		return count != 0;
	}

	public void deleteCommentDislike(Long commentId, UUID userId) {
		commentDislikeMapper.delete(commentId, userId);
	}
}
