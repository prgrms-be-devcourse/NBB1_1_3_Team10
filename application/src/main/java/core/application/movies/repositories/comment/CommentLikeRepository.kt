package core.application.movies.repositories.comment;

import java.util.UUID;

public interface CommentLikeRepository {

    void saveCommentLike(Long commentId, UUID userId);

    boolean isExistLike(Long commentId, UUID userId);

    void deleteCommentLike(Long commentId, UUID userId);
}
