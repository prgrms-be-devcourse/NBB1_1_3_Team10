package core.application.movies.repositories.comment.mybatis

import core.application.movies.models.dto.response.CommentRespDTO
import core.application.movies.models.entities.CommentEntity
import core.application.movies.repositories.comment.CommentRepository
import core.application.movies.repositories.mapper.CommentMapper
import core.application.users.exception.UserExceptionHandler
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@Profile("mybatis")
class MybatisCommentRepository(
    private val commentMapper: CommentMapper,
    private val userExceptionHandler: UserExceptionHandler
) : CommentRepository {

    override fun saveNewComment(movieId: String?, userId: UUID?, comment: CommentEntity?): CommentEntity? {
        // CommentEntity가 null인지 체크
        if (comment == null) {
            return null // comment가 null이면 null 반환
        }

        commentMapper.save(movieId, userId, comment)

        // commentId를 통해 댓글을 찾고, Optional에서 값을 안전하게 추출
        return findByCommentId(comment.commentId)?.let { optionalEntity ->
            if (optionalEntity.isPresent) {
                optionalEntity.get() // Optional이 값을 가지고 있을 경우 해당 값 반환
            } else {
                null // 값이 없을 경우 null 반환
            }
        }
    }



    override fun findByCommentId(commentId: Long?): Optional<CommentEntity?>? {
        return commentMapper.findByCommentId(commentId)
    }

    override fun existsByMovieIdAndUserId(movieId: String?, userId: UUID?): Boolean? {
        return commentMapper.findByMovieIdAndUserId(movieId, userId)?.isPresent
    }

    override fun findByMovieId(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>? {
        val pageable: Pageable = PageRequest.of(page, 10)
        val total = commentMapper.countByMovieId(movieId)
        val find: List<CommentRespDTO> = commentMapper.findByMovieId(movieId, userId, page * 10)?.mapNotNull { it } ?: emptyList()
        return PageImpl(find, pageable, total.toLong())
    }

    override fun findByMovieIdOnDateDescend(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>? {
        val pageable: Pageable = PageRequest.of(page, 10)
        val total = commentMapper.countByMovieId(movieId)
        val find: List<CommentRespDTO> = commentMapper.findByMovieId(movieId, userId, page * 10)?.mapNotNull { it } ?: emptyList()
        return PageImpl(find, pageable, total.toLong())
    }

    override fun findByMovieIdOnLikeDescend(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>? {
        val pageable: Pageable = PageRequest.of(page, 10)
        val total = commentMapper.countByMovieId(movieId)
        val find: List<CommentRespDTO> = commentMapper.findByMovieId(movieId, userId, page * 10)?.mapNotNull { it } ?: emptyList()
        return PageImpl(find, pageable, total.toLong())
    }

    override fun findByMovieIdOnDislikeDescend(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>? {
        val pageable: Pageable = PageRequest.of(page, 10)
        val total = commentMapper.countByMovieId(movieId)
        val find: List<CommentRespDTO> = commentMapper.findByMovieId(movieId, userId, page * 10)?.mapNotNull { it } ?: emptyList()
        return PageImpl(find, pageable, total.toLong())
    }

    override fun selectAll(): List<CommentEntity?>? {
        return commentMapper.selectAll()
    }

    override fun update(comment: CommentEntity?) {
        commentMapper.update(comment)
    }

    override fun deleteComment(commentId: Long?) {
        commentMapper.delete(commentId)
    }
}