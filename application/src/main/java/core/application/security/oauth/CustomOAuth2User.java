package core.application.security.oauth;

import core.application.users.models.dto.UserDTO;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * OAuth2 인증을 위한 사용자 정보를 담는 클래스
 */
@ToString
public class CustomOAuth2User implements OAuth2User {

    private final UserDTO userDTO;

    public CustomOAuth2User(final UserDTO userDTO) {
        this.userDTO = userDTO;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                return userDTO.getRole().toString();
            }
        });
        return authorities;
    }

    @Override
    public String getName() {
        return userDTO.getUserName();
    }

    public String getUserEmail() {
        return userDTO.getUserEmail();
    }

    public UUID getUserId() {
        return userDTO.getUserId();
    }

    public String getAlias() {
        return userDTO.getAlias();
    }

    public String getUserRole() { return userDTO.getRole().toString(); }
}
