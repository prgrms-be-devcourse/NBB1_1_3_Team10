package core.application.movies.repositories.comment.jpa;

import core.application.movies.models.entities.CommentLike;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface JpaCommentLikeRepository extends JpaRepository<CommentLike, Long> {

    @Modifying
    @Query(value = "insert into comment_like_table(comment_id, user_id) values (:commentId, :userId)",
    nativeQuery = true)
    void saveLike(Long commentId, UUID userId);

    Boolean existsByComment_CommentIdAndUserId(Long commentId, UUID userId);

    void deleteByComment_CommentIdAndUserId(Long commentId, UUID userId);
}
