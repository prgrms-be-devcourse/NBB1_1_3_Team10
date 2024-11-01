package core.application.movies.repositories.comment

import core.application.movies.models.dto.response.CommentRespDTO
import core.application.movies.models.entities.CommentEntity
import org.springframework.data.domain.Page
import java.util.*

/**
 * `COMMENT_TABLE` 과 관련된 `Repository`
 */
interface CommentRepository {
    // CREATE
    /**
     * 특정 영화에 주어진 유저 ID 로 새로운 한줄평 댓글을 DB 에 등록
     *
     * @param movieId 한줄평 댓글을 등록할 영화 ID
     * @param userId  댓글을 등록하는 유저 ID
     * @param comment 새로운 한줄평 댓글
     * @return [CommentEntity] 등록된 정보
     */
    fun saveNewComment(movieId: String?, userId: UUID?, comment: CommentEntity?): CommentEntity?

    //<editor-fold desc="READ">
    /**
     * 한줄평 댓글 ID 로 검색
     *
     * @param commentId 댓글 ID
     * @return [Optional]`<`[CommentEntity]`>`
     */
    fun findByCommentId(commentId: Long?): Optional<CommentEntity?>?

    /**
     *
     * @param movieId 영화 ID
     * @param userId 유저 ID
     * @return 사용자가 해당 영화에 한줄평을 작성한 기록이 있는지 확인
     */
    fun existsByMovieIdAndUserId(movieId: String?, userId: UUID?): Boolean?

    /**
     * 특정 영화에 달린 한줄평 댓글들을 검색
     *
     * @param movieId 검색할 영화 ID
     * @return [List]`<`[CommentEntity]`>`
     * @see .findByMovieIdOnDateDescend
     * @see .findByMovieIdOnLikeDescend
     * @see .findByMovieIdOnDislikeDescend
     */
    fun findByMovieId(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>?

    /**
     * 특정 영화에 달린 한줄평 댓글을 최신순으로 검색
     *
     * @param movieId 검색할 영화 ID
     * @return [List]`<`[CommentEntity]`>`
     */
    fun findByMovieIdOnDateDescend(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>?

    /**
     * 특정 영화에 달린 한줄평 댓글을 좋아요 순으로 검색
     *
     * @param movieId 검색할 영화 ID
     * @return [List]`<`[CommentEntity]`>`
     */
    fun findByMovieIdOnLikeDescend(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>?

    /**
     * 특정 영화에 달린 한줄평 댓글을 싫어요 순으로 검색
     *
     * @param movieId 검색할 영화 ID
     * @return [List]`<`[CommentEntity]`>`
     */
    fun findByMovieIdOnDislikeDescend(movieId: String?, userId: UUID?, page: Int): Page<CommentRespDTO?>?

    /**
     * DB 의 모든 한줄평 댓글을 검색
     *
     * @return [List]`<`[CommentEntity]`>`
     */
    fun selectAll(): List<CommentEntity?>?

    //</editor-fold>
    // UPDATE
    /**
     * 한줄평 내용 수정
     * @param comment 수정할 한줄평
     */
    fun update(comment: CommentEntity?)

    // DELETE
    /**
     * 특정 한줄평 댓글을 삭제
     *
     * @param commentId 삭제할 한줄평 댓글의 ID
     */
    fun deleteComment(commentId: Long?)
}
