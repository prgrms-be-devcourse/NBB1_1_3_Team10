package core.application.security.model

interface OAuth2Response {
    // Resource Server
    val provider: String?

    // Resource Server에서 제공해주는 id
    val providerId: String?

    val email: String?

    val name: String?

    val alias: String?

    override fun toString(): String
}
