package core.application.reviews.models.dto.response.comments;

import core.application.reviews.models.entities.*;

public class EditCommentRespDTO extends CommonCommentRespDTO {

    public static EditCommentRespDTO toDTO(ReviewCommentEntity entity) {
        return (EditCommentRespDTO) new EditCommentRespDTO()
                .setReviewCommentId(entity.getReviewCommentId())
                .setGroupId(entity.getGroupId())
                .setCommentRef(entity.getCommentRef())
                .setUserId(entity.getUserId())
                .setContent(entity.getContent())
                .setLikeCount(entity.getLike())
                .setCreatedAt(entity.getCreatedAt())
                .setUpdated(entity.isUpdated());
    }
}
