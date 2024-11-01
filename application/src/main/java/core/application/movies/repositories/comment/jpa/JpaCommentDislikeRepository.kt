package core.application.movies.repositories.comment.jpa

import core.application.movies.models.entities.CommentDislike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface JpaCommentDislikeRepository : JpaRepository<CommentDislike?, Long?> {
    @Modifying
    @Query(
        value = "insert into comment_dislike_table(comment_id, user_id) values (:commentId, :userId)",
        nativeQuery = true
    )
    fun saveDisLike(commentId: Long?, userId: UUID?)

    fun existsByComment_CommentIdAndUserId(commentId: Long?, userId: UUID?): Boolean?

    fun deleteByComment_CommentIdAndUserId(commentId: Long?, userId: UUID?)
}
