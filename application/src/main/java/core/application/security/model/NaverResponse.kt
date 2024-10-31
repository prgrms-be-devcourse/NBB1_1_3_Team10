package core.application.security.model

/**
 * 네이버 로그인 인증 사용자 정보를 담는 클래스
 */
class NaverResponse(attributes: Map<String?, Any?>) : OAuth2Response {
    private val attributes =
        attributes["response"] as Map<String, Any>?

    override fun getProvider(): String {
        return "naver"
    }

    override fun getProviderId(): String {
        return attributes!!["id"].toString()
    }

    override fun getEmail(): String {
        return attributes!!["email"].toString()
    }

    override fun getName(): String {
        return attributes!!["name"].toString()
    }

    override fun getAlias(): String {
        return attributes!!["email"].toString().split("@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
    }

    override fun toString(): String {
        return "NaverResponse [attributes=$attributes]"
    }
}
