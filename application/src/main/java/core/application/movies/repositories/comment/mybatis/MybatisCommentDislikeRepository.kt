package core.application.movies.repositories.comment.mybatis

import core.application.movies.repositories.comment.CommentDislikeRepository
import core.application.movies.repositories.mapper.CommentDislikeMapper
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
@Profile("mybatis")
abstract class MybatisCommentDislikeRepository(
    private val commentDislikeMapper: CommentDislikeMapper
) : CommentDislikeRepository {

    fun saveCommentDislike(commentId: Long, userId: UUID) {
        commentDislikeMapper.save(commentId, userId)
    }

    fun isExistDislike(commentId: Long, userId: UUID): Boolean {
        val count = commentDislikeMapper.countLikeByUser(commentId, userId)
        return count != 0
    }

    fun deleteCommentDislike(commentId: Long, userId: UUID) {
        commentDislikeMapper.delete(commentId, userId)
    }
}

