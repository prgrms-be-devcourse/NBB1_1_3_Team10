package core.application.security.model;

import java.util.Map;

/**
 * 네이버 로그인 인증 사용자 정보를 담는 클래스
 */
public class NaverResponse implements OAuth2Response {
    private final Map<String, Object> attributes;

    public NaverResponse(final Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getAlias() {
        return attributes.get("email").toString().split("@")[0];
    }

    @Override
    public String toString() {
        return "NaverResponse [attributes=" + attributes + "]";
    }
}
