package core.application.movies.repositories.comment.jpa

import core.application.movies.repositories.comment.CommentDislikeRepository
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@RequiredArgsConstructor
@Profile("jpa")
class CommentDislikeRepositoryJPAImpl (
    private val jpaRepository: JpaCommentDislikeRepository
): CommentDislikeRepository {

    override fun saveCommentDislike(commentId: Long?, userId: UUID?) {
        jpaRepository.saveDisLike(commentId, userId)
    }

    override fun isExistDislike(commentId: Long?, userId: UUID?): Boolean {
        return jpaRepository.existsByComment_CommentIdAndUserId(commentId, userId)!!
    }

    override fun deleteCommentDislike(commentId: Long?, userId: UUID?) {
        jpaRepository.deleteByComment_CommentIdAndUserId(commentId, userId)
    }
}
