package core.application.movies.controller

import core.application.api.response.ApiResponse
import core.application.api.response.code.Message
import core.application.movies.constant.CommentSort
import core.application.movies.exception.InvalidWriteCommentException
import core.application.movies.models.dto.request.CommentWriteReqDTO
import core.application.movies.models.dto.response.CommentRespDTO
import core.application.movies.service.CommentService
import core.application.security.auth.CustomUserDetails
import core.application.users.models.entities.UserEntity
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.domain.Page
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.UUID

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
@Tag(name = "Comment", description = "한줄평 관련 API")
class CommentController(private val commentService: CommentService) {

    @Operation(summary = "한줄평 조회")
    @Parameters(
        Parameter(name = "page", description = "페이지", example = "0"),
        Parameter(name = "sortType", description = "정렬 타입", example = "LIKE")
    )
    @GetMapping("/{movieId}/comments")
    fun getComments(
        @PathVariable("movieId") movieId: String,
        @RequestParam("page") page: Int,
        @RequestParam("sortType") sortType: String,
        @AuthenticationPrincipal userDetails: CustomUserDetails?
    ): ApiResponse<Page<CommentRespDTO?>?>? {
        val userId: UUID? = userDetails?.userId
        val comments: Page<CommentRespDTO?>?

        // 잘못된 정렬 타입은 좋아요 순으로 제공한다.
        if (!CommentSort.isValid(sortType)) {
            comments = commentService.getComments(movieId, page, CommentSort.LIKE, userId)
        } else {
            val sort = CommentSort.valueOf(sortType)
            comments = commentService.getComments(movieId, page, sort, userId)
        }
        return ApiResponse.onSuccess(comments)
    }

    @Operation(summary = "한줄평 작성")
    @PostMapping("/{movieId}/comments")
    fun writeComment(
        @PathVariable("movieId") movieId: String,
        @RequestBody @Validated writeReqDTO: CommentWriteReqDTO,
        bindingResult: BindingResult,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<CommentRespDTO> {
        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생 : '${bindingResult}'")
            throw InvalidWriteCommentException(bindingResult.allErrors[0].defaultMessage)
        }
        val user: UserEntity = userDetails.userEntity
        val commentRespDTO = commentService.writeCommentOnMovie(writeReqDTO, user, movieId)
        return ApiResponse.onCreateSuccess(commentRespDTO)
    }

    @Operation(summary = "한줄평 삭제")
    @DeleteMapping("/{movieId}/comments/{commentId}")
    fun deleteComment(
        @PathVariable("movieId") movieId: String,
        @PathVariable("commentId") commentId: String,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<Message> {
        val userId: UUID? = userDetails.userId
        commentService.deleteCommentOnMovie(movieId, userId, commentId.toLong())
        return ApiResponse.onDeleteSuccess(Message.createMessage("한줄평이 삭제되었습니다."))
    }

    @Operation(summary = "한줄평 좋아요")
    @PostMapping("/{movieId}/comments/{commentId}/like")
    fun incrementCommentLike(
        @PathVariable("commentId") commentId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<Message> {
        val userId: UUID? = userDetails.userId
        commentService.incrementCommentLike(commentId, userId)
        return ApiResponse.onCreateSuccess(Message.createMessage("한줄평 좋아요 성공"))
    }

    @Operation(summary = "한줄평 좋아요 취소")
    @DeleteMapping("/{movieId}/comments/{commentId}/like")
    fun decrementCommentLike(
        @PathVariable("commentId") commentId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<Message> {
        val userId: UUID? = userDetails.userId
        commentService.decrementCommentLike(commentId, userId)
        return ApiResponse.onDeleteSuccess(Message.createMessage("한줄평 좋아요 취소 성공"))
    }

    @Operation(summary = "한줄평 싫어요")
    @PostMapping("/{movieId}/comments/{commentId}/dislike")
    fun incrementCommentDislike(
        @PathVariable("commentId") commentId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<Message> {
        val userId: UUID? = userDetails.userId
        commentService.incrementCommentDislike(commentId, userId)
        return ApiResponse.onCreateSuccess(Message.createMessage("한줄평 싫어요 성공"))
    }

    @Operation(summary = "한줄평 싫어요 취소")
    @DeleteMapping("/{movieId}/comments/{commentId}/dislike")
    fun decrementCommentDislike(
        @PathVariable("commentId") commentId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<Message> {
        val userId: UUID? = userDetails.userId
        commentService.decrementCommentDislike(commentId, userId)
        return ApiResponse.onDeleteSuccess(Message.createMessage("한줄평 싫어요 취소 성공"))
    }
}
