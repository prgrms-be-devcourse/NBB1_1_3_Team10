package core.application.movies.repositories.comment;

import java.util.UUID;

public interface CommentDislikeRepository {

    void saveCommentDislike(Long commentId, UUID userId);

    boolean isExistDislike(Long commentId, UUID userId);

    void deleteCommentDislike(Long commentId, UUID userId);
}
