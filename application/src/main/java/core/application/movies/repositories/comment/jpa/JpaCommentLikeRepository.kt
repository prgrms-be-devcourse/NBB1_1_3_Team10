package core.application.movies.repositories.comment.jpa

import core.application.movies.models.entities.CommentLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*

interface JpaCommentLikeRepository : JpaRepository<CommentLike?, Long?> {
    @Modifying
    @Query(
        value = "insert into comment_like_table(comment_id, user_id) values (:commentId, :userId)",
        nativeQuery = true
    )
    fun saveLike(commentId: Long?, userId: UUID?)

    fun existsByComment_CommentIdAndUserId(commentId: Long?, userId: UUID?): Boolean?

    fun deleteByComment_CommentIdAndUserId(commentId: Long?, userId: UUID?)
}
