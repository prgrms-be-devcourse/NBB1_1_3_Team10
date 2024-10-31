package core.application.users.models.dto

import java.util.*

data class MessageResponseDTO (
    val userId: UUID? = null,
    val message: String? = null
)
