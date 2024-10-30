package core.application.movies.repositories.comment.jpa;

import core.application.movies.repositories.comment.CommentDislikeRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Profile("jpa")
public class CommentDislikeRepositoryJPAImpl implements CommentDislikeRepository {

    private final JpaCommentDislikeRepository jpaRepository;

    @Override
    public void saveCommentDislike(Long commentId, UUID userId) {
        jpaRepository.saveDisLike(commentId, userId);
    }

    @Override
    public boolean isExistDislike(Long commentId, UUID userId) {
        return jpaRepository.existsByComment_CommentIdAndUserId(commentId, userId);
    }

    @Override
    public void deleteCommentDislike(Long commentId, UUID userId) {
        jpaRepository.deleteByComment_CommentIdAndUserId(commentId, userId);
    }
}
