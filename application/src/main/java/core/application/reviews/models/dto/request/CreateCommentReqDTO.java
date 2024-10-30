package core.application.reviews.models.dto.request;

import core.application.reviews.models.entities.ReviewCommentEntity;
import java.util.UUID;

public class CreateCommentReqDTO extends CommonCommentReqDTO {

    public ReviewCommentEntity toEntity(UUID userId) {
        return ReviewCommentEntity.builder()
                .groupId(groupId)
                .commentRef(commentRef)
                .content(content)
                .build();
    }
}
