package core.application.movies.service

import core.application.movies.constant.CommentSort
import core.application.movies.exception.*
import core.application.movies.models.dto.request.CommentWriteReqDTO
import core.application.movies.models.dto.response.CommentRespDTO
import core.application.movies.models.dto.response.CommentRespDTO.Companion.of
import core.application.movies.models.entities.CommentEntity.Companion.of
import core.application.movies.repositories.comment.CommentDislikeRepository
import core.application.movies.repositories.comment.CommentLikeRepository
import core.application.movies.repositories.comment.CommentRepository
import core.application.movies.repositories.movie.CachedMovieRepository
import core.application.users.models.entities.UserEntity
import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Supplier

@Service
class CommentService(
    private val movieRepository: CachedMovieRepository,
    private val commentRepository: CommentRepository,
    private val likeRepository: CommentLikeRepository,
    private val dislikeRepository: CommentDislikeRepository
) {

    private val log = LoggerFactory.getLogger(CommentService::class.java)

    @Transactional(readOnly = true)
    fun getComments(movieId: String?, page: Int, sort: CommentSort, userId: UUID?): Page<CommentRespDTO?>? {
        return when (sort) {
            CommentSort.LIKE -> commentRepository.findByMovieIdOnLikeDescend(movieId, userId, page)
            CommentSort.LATEST -> commentRepository.findByMovieIdOnDateDescend(movieId, userId, page)
            else -> commentRepository.findByMovieIdOnDislikeDescend(movieId, userId, page)
        }
    }

    @Transactional
    fun writeCommentOnMovie(writeReqDTO: CommentWriteReqDTO, user: UserEntity, movieId: String): CommentRespDTO {
        // 이미 작성한 기록이 있는지 확인한다.
        log.info("user = {}", user)

        if (commentRepository.existsByMovieIdAndUserId(movieId, user.userId) == true) {
            throw InvalidWriteCommentException("한줄평은 1회 작성만 가능합니다.")
        }
        val newComment = of(writeReqDTO, movieId, user.userId)
        val save = commentRepository.saveNewComment(movieId, user.userId, newComment)

        val movie = movieRepository.findByMovieId(movieId)
            ?.orElseThrow(Supplier { NoMovieException("존재하지 않는 영화입니다.") })

        if (movie != null) {
            log.info("수정 전 영화 총 평점 : {}, 수정 전 영화 한줄평 개수 : {}", movie.sumOfRating, movie.commentCount)
        }
        movie?.isCommentedWithRating(newComment.rating)
        if (movie != null) {
            log.info("수정된 영화 총 평점 : {}, 수정된 영화 한줄평 개수 : {}", movie.sumOfRating, movie.commentCount)
        }
        movieRepository.editMovie(movieId, movie)
        return of(save!!, user.alias)
    }

    @Transactional
    fun deleteCommentOnMovie(movieId: String, userId: UUID, commentId: Long?) {
        val comment = commentRepository.findByCommentId(commentId)
            ?.orElseThrow(Supplier { NotFoundCommentException("존재하지 않는 한줄평입니다.") })
        if (comment!!.userId != userId) {
            throw NotCommentWriterException("한줄평 작성자가 아닙니다.")
        }
        if (comment.movieId != movieId) {
            throw NotMatchMovieCommentException("해당 영화의 한줄평이 아닙니다.")
        }

        commentRepository.deleteComment(commentId)
        val movie = movieRepository.findByMovieId(movieId)
            ?.orElseThrow(Supplier { NoMovieException("존재하는 영화가 아닙니다.") })!!
        log.info("[MovieService.deleteCommentOnMovie] 영화 정보 수정")
        log.info(
            "[MovieService.deleteCommentOnMovie] before rating : {}, commentCount : {}", movie.sumOfRating,
            movie.commentCount
        )
        movie.deleteComment(comment.rating)
        log.info(
            "[MovieService.deleteCommentOnMovie] before rating : {}, commentCount : {}", movie.sumOfRating,
            movie.commentCount
        )
        movieRepository.editMovie(movieId, movie)
    }

    @Transactional
    fun incrementCommentLike(commentId: Long?, userId: UUID?) {
        val comment = commentRepository.findByCommentId(commentId)
            ?.orElseThrow(Supplier { NotFoundCommentException("존재하지 않는 한줄평입니다.") })
        if (likeRepository.isExistLike(commentId, userId)) {
            throw InvalidReactionException("이미 '좋아요'를 누른 한줄평입니다.")
        }
        comment!!.isLiked()
        commentRepository.update(comment)
        likeRepository.saveCommentLike(commentId, userId)
    }

    @Transactional
    fun decrementCommentLike(commentId: Long?, userId: UUID?) {
        val comment = commentRepository.findByCommentId(commentId)
            ?.orElseThrow(Supplier { NotFoundCommentException("존재하지 않는 한줄평입니다.") })
        if (!likeRepository.isExistLike(commentId, userId)) {
            throw InvalidReactionException("'좋아요'를 누르지 않은 한줄평입니다.")
        }
        comment!!.cancelLike()
        commentRepository.update(comment)
        likeRepository.deleteCommentLike(commentId, userId)
    }

    @Transactional
    fun incrementCommentDislike(commentId: Long?, userId: UUID?) {
        val comment = commentRepository.findByCommentId(commentId)
            ?.orElseThrow(Supplier { NotFoundCommentException("존재하지 않는 한줄평입니다.") })
        if (dislikeRepository.isExistDislike(commentId, userId)) {
            throw InvalidReactionException("이미 '싫어요'를 누른 한줄평입니다.")
        }
        comment!!.isDisliked()
        commentRepository.update(comment)
        dislikeRepository.saveCommentDislike(commentId, userId)
    }

    @Transactional
    fun decrementCommentDislike(commentId: Long, userId: UUID) {
        val comment = commentRepository.findByCommentId(commentId)
            ?.orElseThrow(Supplier { NotFoundCommentException("존재하지 않는 한줄평입니다.") })
        if (!dislikeRepository.isExistDislike(commentId, userId)) {
            throw InvalidReactionException("'싫어요'를 누르지 않은 한줄평입니다.")
        }
        comment!!.cancelDislike()
        commentRepository.update(comment)
        dislikeRepository.deleteCommentDislike(commentId, userId)
    }
}
