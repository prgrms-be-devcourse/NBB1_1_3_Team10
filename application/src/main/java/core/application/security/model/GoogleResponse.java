package core.application.security.model;

import java.util.Map;

/**
 * 구글 로그인 인증 사용자 정보를 담는 클래스
 */
public class GoogleResponse implements OAuth2Response {
    private final Map<String, Object> attributes;

    public GoogleResponse(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
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
        return "GoogleResponse [attributes=" + attributes + "]";
    }
}
