package core.application.security.model

class NaverResponse(attributes: Map<String, Any>) : OAuth2Response {
    private val attributes: Map<String, Any> = attributes["response"] as Map<String, Any>

    override val provider: String
        get() = "naver"

    override val providerId: String
        get() = attributes["id"].toString()

    override val email: String
        get() = attributes["email"].toString()

    override val name: String
        get() = attributes["name"].toString()

    override val alias: String
        get() = email.split("@")[0]

    override fun toString(): String {
        return "NaverResponse(attributes=$attributes)"
    }
}
