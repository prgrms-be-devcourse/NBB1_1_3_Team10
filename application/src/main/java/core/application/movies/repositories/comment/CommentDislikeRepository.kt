package core.application.movies.repositories.comment

import java.util.*

interface CommentDislikeRepository {
    fun saveCommentDislike(commentId: Long?, userId: UUID?)

    fun isExistDislike(commentId: Long?, userId: UUID?): Boolean

    fun deleteCommentDislike(commentId: Long?, userId: UUID?)
}
