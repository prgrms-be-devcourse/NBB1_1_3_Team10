package core.application.reviews.models.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class CommonCommentReqDTO(
    @Schema(description = "생성할 댓글의 부모 댓글 ID. 현재 생성하는 댓글이 부모 댓글이면 null", nullable = true)
    var groupId: Long,

    @Schema(description = "생성할 댓글이 멘션할 댓글 ID. 멘션할 댓글이 없으면 null", nullable = true)
    var commentRef: Long,

    @Schema(description = "댓글의 내용", example = "댓글 내용")
    var content: @NotNull(message = "댓글 내용이 존재하지 않습니다.") @NotEmpty(message = "Content can not be empty") String,
)
