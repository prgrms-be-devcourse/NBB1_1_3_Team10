package core.application.movies.models.dto.response

import lombok.AllArgsConstructor
import lombok.Data

@Data
@AllArgsConstructor
class CommentReactionRespDTO {
    private val message: String? = null
}