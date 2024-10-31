package core.application.movies.repositories.comment.jpa;

import core.application.movies.models.entities.CommentDislike;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface JpaCommentDislikeRepository extends JpaRepository<CommentDislike, Long> {

    @Modifying
    @Query(value = "insert into comment_dislike_table(comment_id, user_id) values (:commentId, :userId)", nativeQuery = true)
    void saveDisLike(Long commentId, UUID userId);

    Boolean existsByComment_CommentIdAndUserId(Long commentId, UUID userId);

    void deleteByComment_CommentIdAndUserId(Long commentId, UUID userId);
}
