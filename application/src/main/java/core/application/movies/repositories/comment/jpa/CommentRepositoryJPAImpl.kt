package core.application.movies.repositories.comment.jpa

import core.application.movies.models.dto.response.CommentRespDTO
import core.application.movies.models.entities.CommentEntity
import core.application.movies.repositories.comment.CommentRepository
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.util.*

@Repository
@RequiredArgsConstructor
@Profile("jpa")
class CommentRepositoryJPAImpl(
    private val jpaRepository: JpaCommentRepository
) : CommentRepository {

    override fun saveNewComment(movieId: String?, userId: UUID?, comment: CommentEntity?): CommentEntity? {
        return comment?.let { jpaRepository.save(it) }
    }

    override fun findByCommentId(commentId: Long?): Optional<CommentEntity?>? {
        return commentId?.let { jpaRepository.findById(it) }
    }

    override fun existsByMovieIdAndUserId(movieId: String?, userId: UUID?): Boolean? {
        return jpaRepository.existsByMovieIdAndUserId(movieId, userId)
    }

    override fun findByMovieId(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>? {
        return jpaRepository.findByMovieId(movieId, userId, PageRequest.of(page, 10))
    }

    override fun findByMovieIdOnDateDescend(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>? {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        return jpaRepository.findByMovieIdOrderBy(movieId, userId, pageable)
    }

    override fun findByMovieIdOnLikeDescend(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>? {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "like"))
        return jpaRepository.findByMovieIdOrderBy(movieId, userId, pageable)
    }

    override fun findByMovieIdOnDislikeDescend(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>? {
        val pageable: Pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "dislike"))
        return jpaRepository.findByMovieIdOrderBy(movieId, userId, pageable)
    }

    override fun selectAll(): List<CommentEntity?>? {
        return jpaRepository.findAll()
    }

    override fun update(comment: CommentEntity?) {
        if (comment != null) {
            jpaRepository.save(comment)
        }
    }

    override fun deleteComment(commentId: Long?) {
        if (commentId != null) {
            jpaRepository.deleteById(commentId)
        }
    }
}
