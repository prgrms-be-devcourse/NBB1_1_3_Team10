package core.application.reviews.controllers

import core.application.api.response.ApiResponse
import core.application.api.response.code.Message
import core.application.reviews.exceptions.InvalidPageException
import core.application.reviews.exceptions.InvalidReviewEditException
import core.application.reviews.exceptions.InvalidReviewWriteException
import core.application.reviews.exceptions.NotReviewOwnerException
import core.application.reviews.models.dto.request.reviews.CreateReviewReqDTO
import core.application.reviews.models.dto.request.reviews.EditReviewReqDTO
import core.application.reviews.models.dto.response.reviews.AdjustLikeRespDTO
import core.application.reviews.models.dto.response.reviews.ListReviewsRespDTO
import core.application.reviews.models.dto.response.reviews.ReviewInfoRespDTO
import core.application.reviews.models.dto.response.reviews.ReviewInfoRespDTO.Companion.valueOf
import core.application.reviews.models.entities.ReviewEntity
import core.application.reviews.services.ReviewService
import core.application.reviews.services.ReviewSortOrder
import core.application.security.auth.CustomUserDetails
import core.application.users.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
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

@RequestMapping("/movies/{movieId}/reviews")
@Tag(name = "Review", description = "Review 관련 API")
@RestController
class ReviewController(
    private val reviewService: ReviewService,
    private val userService: UserService
) {


    /**
     * 특정 영화에 달린 포스팅을 보여주는 엔드포인트
     *
     * @param movieId 영화 ID
     * @param page    페이징 넘버
     * @param sort    정렬 순서 `(latest | like)`
     * @param content 본문을 포함해 응답할지 `Y/N`
     * @return 응답용 포스팅 목록들
     * @see ReviewSortOrder
     */
    @Operation(summary = "리뷰 목록 조회")
    @GetMapping("/list")
    fun listReviews(
        @PathVariable("movieId") movieId: String,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "sort", required = false, defaultValue = "latest") sort: String,
        @RequestParam(name = "content", defaultValue = "true") content: Boolean
    ): ApiResponse<Page<ListReviewsRespDTO>> {
        if (page < 0) {
            throw InvalidPageException("잘못된 리뷰 페이지입니다.")
        }

        val offset = page * REVIEWS_PER_PAGE

        val order = if (Arrays.stream(ReviewSortOrder.entries.toTypedArray())
                .anyMatch { r: ReviewSortOrder -> r.name.equals(sort, ignoreCase = true) }
        ) ReviewSortOrder.valueOf(sort.uppercase(Locale.getDefault())) else ReviewSortOrder.LATEST

        val searchResult = reviewService.getReviewsOnMovieId(
            movieId, order, content, offset, REVIEWS_PER_PAGE
        )
            .stream().map { result -> ListReviewsRespDTO.of(result) }
            .toList()

        val total = reviewService.getNumberOfReviewsOnMovieId(movieId)

        val paged: Page<ListReviewsRespDTO> = PageImpl(
            searchResult,
            PageRequest.of(page, REVIEWS_PER_PAGE),
            total
        )

        return ApiResponse.onSuccess(paged)
    }

    /**
     * 특정 포스팅의 정보를 보여주는 앤드포인트
     *
     * @param reviewId 포스팅 ID
     * @return 포스팅 정보를 담은 DTO
     */
    @Operation(summary = "특정 리뷰 조회")
    @GetMapping("/{reviewId}")
    fun getReviewInfo(@PathVariable("reviewId") reviewId: Long): ApiResponse<ReviewInfoRespDTO> {
        val searchResult = reviewService.getReviewInfo(reviewId, true)

        // TODO 주어진 userId 에 해당하는 사용자가 없으면 던지는 exception 이 없네...?
        // TODO 나중에 생기면 orElseThrow 에 추가
        val userAlias = userService.getUserByUserId(searchResult.userId)!!
            .orElseThrow()!!
            .alias

        return ApiResponse.onSuccess(valueOf(userAlias!!, searchResult))
    }

    /**
     * 새로운 포스팅을 생성하는 엔드포인트
     *
     * @param movieId     포스팅할 영화 ID
     * @param userDetails `Security context holder` 에 존재하는 유저 `principal`
     * @param reqDTO      포스팅 생성 요청 `DTO`
     */
    @Operation(summary = "리뷰 작성")
    @PostMapping
    fun createReview(
        @PathVariable("movieId") movieId: String,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestBody @Validated reqDTO: CreateReviewReqDTO,
        bindingResult: BindingResult
    ): ApiResponse<Message> {
        if (bindingResult.hasErrors()) {
            throw InvalidReviewWriteException(
                bindingResult.allErrors[0].defaultMessage
            )
        }

        // Spring context holder 에서 CustomUserDetails 가져오고
        // CustomUserDetails 에서 userId 꺼냄
        val userId = userDetails.userId!!

        // 해당하는 영화가 DB 에 없으면 createNewReview 에서 throw
        val result = reviewService.createNewReview(
            movieId, userId,
            reqDTO.title.trim { it <= ' ' },
            reqDTO.content
        )

        return ApiResponse.onCreateSuccess(Message.createMessage("성공적으로 글을 작성하였습니다."))
    }

    /**
     * 특정 포스팅을 수정하는 엔드포인트
     *
     * @param reviewId    수정할 포스팅 ID
     * @param userDetails `Security context holder` 에 존재하는 유저 `principal`
     * @param reqDTO      수정 요청 `DTO`
     */
    @Operation(summary = "리뷰 수정")
    @PatchMapping("/{reviewId}")
    fun updateReview(
        @PathVariable("reviewId") reviewId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestBody @Validated reqDTO: EditReviewReqDTO,
        bindingResult: BindingResult
    ): ApiResponse<Message> {
        if (bindingResult.hasErrors()) {
            throw InvalidReviewEditException(
                bindingResult.allErrors[0].defaultMessage
            )
        }

        // Spring context holder 에서 CustomUserDetails 가져오고
        // CustomUserDetails 에서 userId 꺼냄
        val userId = userDetails.userId

        // reviewId 해당 포스팅 없으면 getReviewInfo 에서 throw
        val origin = reviewService.getReviewInfo(reviewId, false)

        println(userId)
        println(origin.userId)
        // 작성자 확인
        if (userId != origin.userId) {
            throw NotReviewOwnerException("글 작성자만 수정할 수 있습니다.")
        }

        val replacement: ReviewEntity = ReviewEntity(
            title = reqDTO.title.trim(),
            content = reqDTO.content
        )

        val result = reviewService.updateReviewInfo(reviewId, replacement)

        return ApiResponse.onSuccess(Message.createMessage("성공적으로 글을 수정하였습니다."))
    }

    /**
     * 특정 포스팅을 삭제하는 엔드포인트
     *
     * @param reviewId    삭제할 포스팅 ID
     * @param userDetails `Security context holder` 에 존재하는 유저 `principal`
     */
    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    fun deleteReview(
        @PathVariable("reviewId") reviewId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ApiResponse<Message> {
        // Spring context holder 에서 CustomUserDetails 가져오고
        // CustomUserDetails 에서 userId 꺼냄

        val userId = userDetails.userId

        // reviewId 해당 포스팅 없으면 getReviewInfo 에서 throw
        val origin = reviewService.getReviewInfo(reviewId, false)

        if (userId != origin.userId) {
            throw NotReviewOwnerException("글 작성자만 삭제할 수 있습니다.")
        }

        val result = reviewService.deleteReview(reviewId)

        return ApiResponse.onSuccess(Message.createMessage("성공적으로 글을 삭제하였습니다."))
    }

    /**
     * 포스팅의 좋아요를 증감시키는 엔드포인트 `(쿠키 이용)`
     *
     * @param reviewId    포스팅 ID
     * @param userDetails `userEmail` 을 가져오기 위한 `principal`
     * @param req         쿠키 가져오기 위한 `request`
     * @param resp        쿠키 저장하기 위한 `response`
     */
    @Operation(summary = "리뷰 좋아요")
    @PatchMapping("/{reviewId}/like")
    fun adjustLike(
        @PathVariable("reviewId") reviewId: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        req: HttpServletRequest, resp: HttpServletResponse
    ): ApiResponse<AdjustLikeRespDTO> {
        // hash 값 이용해서 쿠키 이름, 값 증빙할거임

        val validCookieValue = Objects.hash(reviewId, userDetails.userEmail).toString()

        // request 내 쿠키 중 이름 일치하는 쿠키 확인
        val cookie = if (req.cookies == null) null else Arrays.stream(req.cookies)
            .filter { cookie -> cookie.name == COOKIE_NAME_PREFIX + validCookieValue }
            .findFirst()
            .orElse(null)

        // 쿠키 이름 & 값 일치하는지 확인
        val doesCookieExist = cookie != null && cookie.value == validCookieValue

        // 쿠키 없으면 좋아요 증가, 있으면 감소
        val adjustLike =
            if (doesCookieExist) Function { reviewId: Long -> reviewService.decreaseLikes(reviewId) } else Function { reviewId: Long ->
                reviewService.increaseLikes(reviewId)
            }

        // 쿠키 없으면 쿠키 새로 생성, 있으면 삭제
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

        // 포스팅 좋아요 증감, 쿠키 처리 진행
        val entity = adjustLike.apply(reviewId)!!
        handleCookie.accept(resp, validCookieValue)

        var resultMessage = "리뷰의 좋아요를 " + (if (doesCookieExist) "감소" else "증가") + "시켰습니다."
        resultMessage += " [" + entity.like + "]"

        val adjustLikeRespDTO = AdjustLikeRespDTO(resultMessage)
        return ApiResponse.onSuccess(adjustLikeRespDTO)
    }

    private fun saveCookie(resp: HttpServletResponse, validCookieValue: String) {
        val cookie = Cookie(
            COOKIE_NAME_PREFIX + validCookieValue,
            validCookieValue
        )

        val year = 365 * 24 * 60 * 60
        cookie.maxAge = year
        cookie.isHttpOnly = true

        resp.addCookie(cookie)
    }

    private fun deleteCookie(resp: HttpServletResponse, validCookieValue: String) {
        val cookie = Cookie(
            COOKIE_NAME_PREFIX + validCookieValue,
            validCookieValue
        )
        cookie.maxAge = 0
        resp.addCookie(cookie)
    }

    companion object {
        private const val REVIEWS_PER_PAGE = 10
        private const val COOKIE_NAME_PREFIX = "ReviewLikeAdjustment"
    }
}
