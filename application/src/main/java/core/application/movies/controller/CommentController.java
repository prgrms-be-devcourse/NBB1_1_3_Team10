package core.application.movies.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import core.application.api.response.ApiResponse;
import core.application.api.response.code.Message;
import core.application.movies.constant.CommentSort;
import core.application.movies.exception.InvalidWriteCommentException;
import core.application.movies.models.dto.request.CommentWriteReqDTO;
import core.application.movies.models.dto.response.CommentRespDTO;
import core.application.movies.service.CommentService;
import core.application.security.auth.CustomUserDetails;
import core.application.users.models.entities.UserEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
@Tag(name = "Comment", description = "한줄평 관련 API")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "한줄평 조회")
    @Parameters({
            @Parameter(name = "page", description = "페이지", example = "0"),
            @Parameter(name = "sortType", description = "정렬 타입", example = "LIKE")
    })
    @GetMapping("/{movieId}/comments")
    public ApiResponse<Page<CommentRespDTO>> getComments(@PathVariable("movieId") String movieId,
                                                         @RequestParam("page") int page, @RequestParam("sortType") String sortType, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<CommentRespDTO> comments;
        UUID userId;
        if (userDetails == null) {
            userId = null;
        } else {
            userId = userDetails.getUserId();
        }
        // 잘못된 정렬 타입은 좋아요 순으로 제공한다.
        if (!CommentSort.isValid(sortType)) {
            comments = commentService.getComments(movieId, page, CommentSort.LIKE, userId);
        } else {
            CommentSort sort = CommentSort.valueOf(sortType);
            comments = commentService.getComments(movieId, page, sort, userId);
        }
        return ApiResponse.onSuccess(comments);
    }

    @Operation(summary = "한줄평 작성")
    @PostMapping("/{movieId}/comments")
    public ApiResponse<CommentRespDTO> writeComment(@PathVariable("movieId") String movieId,
                                                    @RequestBody @Validated CommentWriteReqDTO writeReqDTO, BindingResult bindingResult,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (bindingResult.hasErrors()) {
            log.info("검증 오류 발생 : {}", bindingResult);
            throw new InvalidWriteCommentException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        UserEntity user = userDetails.userEntity();
        CommentRespDTO commentRespDTO = commentService.writeCommentOnMovie(writeReqDTO, user, movieId);
        return ApiResponse.onCreateSuccess(commentRespDTO);
    }

    @Operation(summary = "한줄평 삭제")
    @DeleteMapping("/{movieId}/comments/{commentId}")
    public ApiResponse<Message> deleteComment(@PathVariable("movieId") String movieId,
                                              @PathVariable("commentId") String commentId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getUserId();
        commentService.deleteCommentOnMovie(movieId, userId, Long.parseLong(commentId));
        return ApiResponse.onDeleteSuccess(Message.createMessage("한줄평이 삭제되었습니다."));
    }

    @Operation(summary = "한줄평 좋아요")
    @PostMapping("/{movieId}/comments/{commentId}/like")
    public ApiResponse<Message> incrementCommentLike(@PathVariable("commentId") Long commentId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getUserId();
        commentService.incrementCommentLike(commentId, userId);
        return ApiResponse.onCreateSuccess(Message.createMessage("한줄평 좋아요 성공"));
    }

    @Operation(summary = "한줄평 좋아요 취소")
    @DeleteMapping("/{movieId}/comments/{commentId}/like")
    public ApiResponse<Message> decrementCommentLike(@PathVariable("commentId") Long commentId,
                                                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getUserId();
        commentService.decrementCommentLike(commentId, userId);
        return ApiResponse.onDeleteSuccess(Message.createMessage("한줄평 좋아요 취소 성공"));
    }

    @Operation(summary = "한줄평 싫어요")
    @PostMapping("/{movieId}/comments/{commentId}/dislike")
    public ApiResponse<Message> incrementCommentDislike(@PathVariable("commentId") Long commentId,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getUserId();
        commentService.incrementCommentDislike(commentId, userId);
        return ApiResponse.onCreateSuccess(Message.createMessage("한줄평 싫어요 성공"));
    }

    @Operation(summary = "한줄평 싫어요 취소")
    @DeleteMapping("/{movieId}/comments/{commentId}/dislike")
    public ApiResponse<Message> decrementCommentDislike(@PathVariable("commentId") Long commentId,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getUserId();
        commentService.decrementCommentDislike(commentId, userId);
        return ApiResponse.onDeleteSuccess(Message.createMessage("한줄평 싫어요 취소 성공"));
    }
}
