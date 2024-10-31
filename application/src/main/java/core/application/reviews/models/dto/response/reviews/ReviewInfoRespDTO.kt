package core.application.reviews.models.dto.response.reviews;

import core.application.reviews.models.entities.ReviewEntity;
import java.time.Instant;
import lombok.Data;

@Data
public class ReviewInfoRespDTO {

    private String title;
    private String user_alias;
    private String content;
    private int like_num;
    private Instant created_at;
    private Instant updated_at;

    public static ReviewInfoRespDTO valueOf(String userAlias, ReviewEntity reviewEntity) {
        ReviewInfoRespDTO response = new ReviewInfoRespDTO();
        response.title = reviewEntity.getTitle();
        response.user_alias = userAlias;
        response.content = reviewEntity.getContent();
        response.like_num = reviewEntity.getLike();
        response.created_at = reviewEntity.getCreatedAt();
        response.updated_at = reviewEntity.getUpdatedAt();

        return response;
    }
}
