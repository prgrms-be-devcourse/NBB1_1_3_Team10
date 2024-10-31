package core.application.security.model

class GoogleResponse(private val attributes: Map<String, Any>) : OAuth2Response {

    override val provider: String
        get() = "google"

    override val providerId: String
        get() = attributes["sub"].toString()

    override val email: String
        get() = attributes["email"].toString()

    override val name: String
        get() = attributes["name"].toString()

    override val alias: String
        get() = email.split("@")[0]

    override fun toString(): String {
        return "GoogleResponse(attributes=$attributes)"
    }
}
