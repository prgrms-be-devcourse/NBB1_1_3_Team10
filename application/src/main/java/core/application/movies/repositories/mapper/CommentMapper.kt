package core.application.movies.repositories.mapper

import core.application.movies.models.dto.response.CommentRespDTO
import core.application.movies.models.entities.CommentEntity
import org.apache.ibatis.annotations.Mapper
import java.util.*

@Mapper
interface CommentMapper {
    fun save(movieId: String?, userId: UUID?, comment: CommentEntity?)

    fun findByCommentId(commentId: Long?): Optional<CommentEntity?>?

    fun findByMovieIdAndUserId(movieId: String?, userId: UUID?): Optional<CommentEntity?>?

    fun findByMovieId(movieId: String?, userId: UUID?, offset: Int): List<CommentRespDTO?>?

    fun findByMovieIdOnDateDescend(movieId: String?, userId: UUID?, offset: Int): List<CommentRespDTO?>?

    fun findByMovieIdOnLikeDescend(movieId: String?, userId: UUID?, offset: Int): List<CommentRespDTO?>?

    fun findByMovieIdOnDislikeDescend(movieId: String?, userId: UUID?, offset: Int): List<CommentRespDTO?>?

    fun selectAll(): List<CommentEntity?>?

    fun countByMovieId(movieId: String?): Int

    fun update(comment: CommentEntity?)

    fun delete(commentId: Long?)
}
