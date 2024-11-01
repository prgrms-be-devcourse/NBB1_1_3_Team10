package core.application.movies.repositories.comment

import java.util.*

interface CommentLikeRepository {
    fun saveCommentLike(commentId: Long?, userId: UUID?)

    fun isExistLike(commentId: Long?, userId: UUID?): Boolean

    fun deleteCommentLike(commentId: Long?, userId: UUID?)
}
