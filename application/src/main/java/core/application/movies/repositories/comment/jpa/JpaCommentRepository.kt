package core.application.movies.repositories.comment.jpa

import core.application.movies.models.dto.response.CommentRespDTO
import core.application.movies.models.entities.CommentEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface JpaCommentRepository : JpaRepository<CommentEntity?, Long?> {
    fun existsByMovieIdAndUserId(movieId: String?, userId: UUID?): Boolean?

    @Query(
        ("SELECT new core.application.movies.models.dto.response.CommentRespDTO(c.commentId, c.content, c.like, c.dislike, c.rating, c.movieId, u.alias, c.createdAt, " +
                "CASE WHEN l.commentLikeId IS NOT NULL THEN true ELSE false END, " +
                "CASE WHEN d.commentDislikeId IS NOT NULL THEN true ELSE false END) " +
                "FROM CommentEntity c " +
                "LEFT JOIN CommentLike l ON c.commentId = l.comment.commentId AND l.userId = :userId " +
                "LEFT JOIN CommentDislike d ON c.commentId = d.comment.commentId AND d.userId = :userId " +
                "LEFT JOIN UserEntity u ON c.userId = u.userId " +
                "WHERE c.movieId = :movieId")
    )
    fun findByMovieId(movieId: String?, userId: UUID?, pageable: Pageable?): Page<CommentRespDTO?>?

    @Query(
        ("SELECT new core.application.movies.models.dto.response.CommentRespDTO(c.commentId, c.content, c.like, c.dislike, c.rating, c.movieId, u.alias, c.createdAt, " +
                "CASE WHEN l.commentLikeId IS NOT NULL THEN true ELSE false END, " +
                "CASE WHEN d.commentDislikeId IS NOT NULL THEN true ELSE false END) " +
                "FROM CommentEntity c " +
                "LEFT JOIN CommentLike l ON c.commentId = l.comment.commentId AND l.userId = :userId " +
                "LEFT JOIN CommentDislike d ON c.commentId = d.comment.commentId AND d.userId = :userId " +
                "LEFT JOIN UserEntity u ON c.userId = u.userId " +
                "WHERE c.movieId = :movieId")
    )
    fun findByMovieIdOrderBy(movieId: String?, userId: UUID?, pageable: Pageable?): Page<CommentRespDTO?>?
}
