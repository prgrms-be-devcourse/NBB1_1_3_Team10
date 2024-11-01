package core.application.reviews.controllers

import core.application.api.response.ApiResponse
import core.application.api.response.code.Message
import core.application.reviews.exceptions.InvalidCommentContentException
import core.application.reviews.exceptions.InvalidPageException
import core.application.reviews.exceptions.NotCommentOwnerException
import core.application.reviews.models.dto.request.CreateCommentReqDTO
import core.application.reviews.models.dto.response.comments.CreateCommentRespDTO
import core.application.reviews.models.dto.response.comments.EditCommentRespDTO
import core.application.reviews.models.dto.response.comments.ShowCommentsRespDTO
import core.application.reviews.models.entities.ReviewCommentEntity
import core.application.reviews.services.ReviewCommentService
import core.application.reviews.services.ReviewCommentSortOrder
import core.application.security.auth.CustomUserDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Function

@RestController
@RequestMapping("/movies/{movieId}/reviews/{reviewId}")
@Tag(name = "Review Comment", description = "영화 후기 포스팅 댓글과 관련된 API")
class ReviewCommentController(private val reviewCommentService: ReviewCommentService) {
    /**
     * 부모 댓글 보여주는 앤드포인트
     *
     * @param reviewId `pathVariable`
     * @param page     페이징 넘버
     * @return 응답용 댓글 목록들
     */
    @GetMapping("/comments")
    @Operation(summary = "부모 댓글 조회", description = "특정 게시글의 부모 댓글을 페이징 하여 조회")
    @Parameters(
        Parameter(name = "reviewId", description = "댓글을 조회할 게시글 ID", example = "20"),
        Parameter(name = "page", description = "0 보다 큰 페이징 넘버", example = "1")
    )
    fun showParentReviewComments(
        @PathVariable("reviewId") reviewId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int
    ): ApiResponse<Page<ShowCommentsRespDTO>> {
        if (page < 0) {
            throw InvalidPageException("잘못된 댓글 페이지입니다.")
        }

        val offset = page * COMMENTS_PER_PAGE

        val parentReviewComments = reviewCommentService
            .getParentReviewComments(
                reviewId, ReviewCommentSortOrder.LIKE,
                offset, COMMENTS_PER_PAGE
            )
            .stream().map { reviewCommentEntity -> ShowCommentsRespDTO.of(reviewCommentEntity) }
            .toList()

        val total = reviewCommentService.getNumberOfParentComment(reviewId)

        val paged: Page<ShowCommentsRespDTO> = PageImpl(
            parentReviewComments,
            PageRequest.of(page, COMMENTS_PER_PAGE),
            total
        )

        return ApiResponse.onSuccess(paged)
    }

    /**
     * 자식 댓글 보여주는 앤드포인트
     *
     * @param reviewId `pathVariable`
     * @param groupId  `pathVariable`
     * @param page     페이징 넘버
     * @return 응답용 댓글 목록들
     */
    @GetMapping("/comments/{groupId}")
    @Operation(summary = "자식 댓글 조회", description = "특정 게시글 속 부모 댓글의 자식 댓글을 페이징 하여 조회")
    @Parameters(
        Parameter(name = "reviewId", description = "댓글을 조회할 게시글 ID", example = "20"),
        Parameter(name = "groupId", description = "부모 댓글의 ID", example = "10010"),
        Parameter(name = "page", description = "0 보다 큰 페이징 넘버", example = "1")
    )
    fun showChildComments(
        @PathVariable("reviewId") reviewId: Long,
        @PathVariable("groupId") groupId: Long,
        @RequestParam(name = "page", defaultValue = "0") page: Int
    ): ApiResponse<Page<ShowCommentsRespDTO>> {
        if (page < 0) {
            throw InvalidPageException("잘못된 댓글 페이지입니다.")
        }

        val offset = page * COMMENTS_PER_PAGE

        val childReviewComments = reviewCommentService.getChildReviewCommentsOnParent(
            reviewId, groupId, offset, COMMENTS_PER_PAGE
        )
            .stream().map { reviewCommentEntity -> ShowCommentsRespDTO.of(reviewCommentEntity) }
            .toList()

        val total = reviewCommentService.getNumberOfChildComment(groupId)

        val paged: Page<ShowCommentsRespDTO> = PageImpl(
            childReviewComments,
            PageRequest.of(page, COMMENTS_PER_PAGE),
            total
        )

        return ApiResponse.onSuccess(paged)
    }

    /**
     * 댓글 생성하는 엔드포인트
     *
     * @param reviewId          `pathVariable`
     * @param customUserDetails `Security context holder` 에 존재하는 유저 `principal`
     * @param dtoReq            요청 `DTO`
     * @return 응답 `DTO`
     */
    @PostMapping("/comments")
    @Operation(summary = "댓글 생성", description = "특정 게시글에 댓글을 생성")
    @Parameter(name = "reviewId", description = "댓글을 조회할 게시글 ID", example = "20")
    fun createComment(
        @PathVariable("reviewId") reviewId: Long,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody @Validated dtoReq: CreateCommentReqDTO,
        bindingResult: BindingResult
    ): ApiResponse<CreateCommentRespDTO> {
        if (bindingResult.hasErrors()) {
            throw InvalidCommentContentException(
                bindingResult.allErrors[0].defaultMessage
            )
        }

        // principal 로 부터 ID 받음
        val userId = customUserDetails.userId

        val groupId = dtoReq.groupId
        val validData = dtoReq.toEntity(userId)

        val result = if (groupId == null) reviewCommentService.addNewParentReviewComment(
            reviewId,
            userId,
            validData
        ) else reviewCommentService.addNewChildReviewComment(reviewId, groupId, userId, validData)

        return ApiResponse.onCreateSuccess(
            CreateCommentRespDTO.toDTO(
                result!!
            )
        )
    }

    /**
     * 댓글 수정하는 엔드포인트
     *
     * @param reviewCommentId   `pathVariable`
     * @param customUserDetails `Security context holder` 에 존재하는 유저 `principal`
     * @param dtoReq            요청 `DTO`
     * @return 응답 `DTO`
     */
    @Operation(summary = "댓글 수정", description = "작성한 댓글을 수정")
    @PatchMapping("/comments/{reviewCommentId}")
    fun editComment(
        @PathVariable("reviewCommentId") reviewCommentId: Long,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails,
        @RequestBody @Validated dtoReq: CreateCommentReqDTO,
        bindingResult: BindingResult
    ): ApiResponse<EditCommentRespDTO> {
        if (bindingResult.hasErrors()) {
            throw InvalidCommentContentException(
                bindingResult.allErrors[0].defaultMessage
            )
        }

        // principal 로 부터 ID 받음
        val userId = customUserDetails.userId

        if (!reviewCommentService.doesUserOwnsComment(userId, reviewCommentId)) {
            throw NotCommentOwnerException("Only comment owner can edit comments")
        }

        val commentRef = dtoReq.commentRef
        val content = dtoReq.content

        val result = reviewCommentService.editReviewComment(
            reviewCommentId,
            commentRef, content
        )

        return ApiResponse.onSuccess(EditCommentRespDTO.toDTO(result!!))
    }

    /**
     * 댓글 삭제하는 엔드포인트
     *
     * @param reviewCommentId   `pathVariable`
     * @param customUserDetails `Security context holder` 에 존재하는 유저 `principal`
     * @return 응답 `DTO`
     */
    @Operation(summary = "댓글 삭제", description = "작성한 댓글을 삭제")
    @DeleteMapping("/comments/{reviewCommentId}")
    fun deleteComment(
        @PathVariable("reviewCommentId") reviewCommentId: Long,
        @AuthenticationPrincipal customUserDetails: CustomUserDetails
    ): ApiResponse<Message> {
        // principal 로 부터 ID 받음

        val userId = customUserDetails.userId

        if (!reviewCommentService.doesUserOwnsComment(userId, reviewCommentId)) {
            throw NotCommentOwnerException("Only comment owner can delete comments")
        }

        reviewCommentService.deleteReviewComment(reviewCommentId)

        return ApiResponse.onDeleteSuccess(Message.createMessage("성공적으로 댓글을 삭제했습니다."))
    }

    /**
     * 좋아요 증감시키는 엔드포인트
     *
     * @param reviewCommentId `pathVariable`
     * @param userDetails     `userEmail` 가져오기 위한 `principal`
     * @param req             쿠키 가져올 `servletRequest`
     * @param resp            쿠키 저장하고 삭제할 `servletResponse`
     * @return 응답용 `DTO`
     */
    @Operation(summary = "댓글 좋아요 또는 좋아요 취소")
    @PatchMapping("/comments/{reviewCommentId}/like")
    fun editLikes(
        @PathVariable("reviewCommentId") reviewCommentId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        req: HttpServletRequest, resp: HttpServletResponse
    ): ApiResponse<Message> {
        // hash 값 이용해서 쿠키 이름, 값 증빌할 거임

        val validCookieValue = Objects.hash(reviewCommentId, userDetails.userEmail).toString()

        // request 내 쿠키 중 이름 일치하는 쿠키 확인
        val cookie = if (req.cookies == null) null else Arrays.stream(req.cookies)
            .filter { c: Cookie? -> c!!.name == COOKIE_NAME_PREFIX + validCookieValue }
            .findFirst()
            .orElse(null)

        // 쿠키 이름 & 값 일치하는지 확인
        val doesCookieExist = cookie != null && cookie.value == validCookieValue

        // 쿠키 없으면 좋아요 증가, 있으면 감소
        val adjustLike = if (doesCookieExist) Function { reviewCommentId: Long? ->
            reviewCommentService.decreaseCommentLike(
                reviewCommentId!!
            )
        } else Function { reviewCommentId: Long? ->
            reviewCommentService.increaseCommentLike(
                reviewCommentId!!
            )
        }

        // 쿠키 없으면 새로 생성, 있으면 삭제
        val handleCookie = if (doesCookieExist) BiConsumer { resp: HttpServletResponse, validCookieValue: String ->
            this.deleteCookie(
                resp,
                validCookieValue
            )
        } else BiConsumer { resp: HttpServletResponse, validCookieValue: String ->
            this.saveCookie(
                resp,
                validCookieValue
            )
        }

        // 댓글 좋아요 증감, 쿠키 처리 진행
        val entity = adjustLike.apply(reviewCommentId)
        handleCookie.accept(resp, validCookieValue)

        var resultMessage = "댓글의 좋아요를 " + (if (doesCookieExist) "감소" else "증가") + "시켰습니다."
        resultMessage += " [" + entity!!.like + "]"

        return ApiResponse.onSuccess(Message.createMessage(resultMessage))
    }

    private fun saveCookie(resp: HttpServletResponse, validCookieValue: String) {
        val cookie = Cookie(COOKIE_NAME_PREFIX + validCookieValue, validCookieValue)

        val year = 365 * 24 * 60 * 60
        cookie.maxAge = year
        cookie.isHttpOnly = true

        resp.addCookie(cookie)
    }

    private fun deleteCookie(resp: HttpServletResponse, validCookieValue: String) {
        val cookie = Cookie(COOKIE_NAME_PREFIX + validCookieValue, validCookieValue)
        cookie.maxAge = 0
        resp.addCookie(cookie)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ReviewCommentController::class.java)
        private const val COMMENTS_PER_PAGE = 10

        private const val COOKIE_NAME_PREFIX = "ReviewCommentLikeAdjustment"
    }
}
