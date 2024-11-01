package core.application.movies.repositories.comment.mybatis

import core.application.movies.repositories.comment.CommentLikeRepository
import core.application.movies.repositories.mapper.CommentLikeMapper
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@RequiredArgsConstructor
@Profile("mybatis")
class MybatisCommentLikeRepository(
    private val commentLikeMapper: CommentLikeMapper
) : CommentLikeRepository {

    override fun saveCommentLike(commentId: Long?, userId: UUID?) {
        commentLikeMapper.save(commentId, userId)
    }

    override fun isExistLike(commentId: Long?, userId: UUID?): Boolean {
        val count = commentLikeMapper.countLikeByUser(commentId, userId)
        return count != 0
    }

    override fun deleteCommentLike(commentId: Long?, userId: UUID?) {
        commentLikeMapper.delete(commentId, userId)
    }
}
