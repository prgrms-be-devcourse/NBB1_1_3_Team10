package core.application.security.model

/**
 * 구글 로그인 인증 사용자 정보를 담는 클래스
 */
class GoogleResponse(private val attributes: Map<String, Any>) : OAuth2Response {
    override fun getProvider(): String {
        return "google"
    }

    override fun getProviderId(): String {
        return attributes["sub"].toString()
    }

    override fun getEmail(): String {
        return attributes["email"].toString()
    }

    override fun getName(): String {
        return attributes["name"].toString()
    }

    override fun getAlias(): String {
        return attributes["email"].toString().split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
    }

    override fun toString(): String {
        return "GoogleResponse [attributes=$attributes]"
    }
}
