package core.application.movies.repositories.comment.jpa;

import core.application.movies.repositories.comment.CommentLikeRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Profile("jpa")
public class CommentLikeRepositoryJPAImpl implements CommentLikeRepository {

    private final JpaCommentLikeRepository jpaRepository;

    @Override
    public void saveCommentLike(Long commentId, UUID userId) {
        jpaRepository.saveLike(commentId, userId);
    }

    @Override
    public boolean isExistLike(Long commentId, UUID userId) {
        return jpaRepository.existsByComment_CommentIdAndUserId(commentId, userId);
    }

    @Override
    public void deleteCommentLike(Long commentId, UUID userId) {
        jpaRepository.deleteByComment_CommentIdAndUserId(commentId, userId);
    }
}
