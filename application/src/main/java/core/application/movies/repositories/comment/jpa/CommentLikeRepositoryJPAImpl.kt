package core.application.movies.repositories.comment.jpa

import core.application.movies.repositories.comment.CommentLikeRepository
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@RequiredArgsConstructor
@Profile("jpa")
class CommentLikeRepositoryJPAImpl(
    private val jpaRepository: JpaCommentLikeRepository
) : CommentLikeRepository {

    override fun saveCommentLike(commentId: Long?, userId: UUID?) {
        jpaRepository.saveLike(commentId, userId)
    }

    override fun isExistLike(commentId: Long?, userId: UUID?): Boolean {
        return jpaRepository.existsByComment_CommentIdAndUserId(commentId, userId)!!
    }

    override fun deleteCommentLike(commentId: Long?, userId: UUID?) {
        jpaRepository.deleteByComment_CommentIdAndUserId(commentId, userId)
    }
}
