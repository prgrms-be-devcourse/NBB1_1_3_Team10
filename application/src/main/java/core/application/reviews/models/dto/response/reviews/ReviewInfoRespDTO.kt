package core.application.reviews.models.dto.response.reviews

import core.application.reviews.models.entities.ReviewEntity
import lombok.Data
import java.time.Instant

data class ReviewInfoRespDTO(
    var title: String,
    var userAlias: String,
    var content: String?,
    var likeNum:Int? = 0,
    var createdAt: Instant?,
    var updatedAt: Instant?
) {

    companion object {
        @JvmStatic
        fun valueOf(userAlias: String, reviewEntity: ReviewEntity): ReviewInfoRespDTO {
            return ReviewInfoRespDTO(
                title = reviewEntity.title,
                userAlias = userAlias,
                content = reviewEntity.content,
                likeNum = reviewEntity.like,
                createdAt = reviewEntity.createdAt,
                updatedAt = reviewEntity.updatedAt
            )
        }
    }
}
