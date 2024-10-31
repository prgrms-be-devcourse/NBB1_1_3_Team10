package core.application.reviews.models.dto.response.comments;

import java.time.*;
import java.util.*;
import lombok.*;
import lombok.experimental.*;

@Data
@Accessors(chain = true)
public class CommonCommentRespDTO {

    private Long reviewCommentId;
    private Long groupId;
    private Long commentRef;
    private UUID userId;
    private String content;
    private int likeCount;
    private Instant createdAt;
    private boolean isUpdated;
}
