package core.application.security.auth;

import core.application.users.models.entities.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * 사용자 세부정보를 담고 있는 클래스
 * <p>
 * Spring Security에서 사용자의 정보를 담기 위해 구현된 {@link UserDetails} 인터페이스
 * 사용자의 인증 정보(이메일, 비밀번호, 권한 등)를 제공
 */
public record CustomUserDetails(UserEntity userEntity) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                return userEntity.getRole().toString();
            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return userEntity.getUserPw();
    }

    @Override
    public String getUsername() {
        return userEntity.getUserName();
    }

    public String getUserEmail() {
        return userEntity.getUserEmail().toString();
    }

    public UUID getUserId() {
        return userEntity.getUserId();
    }

    public String getUserRole() {
        return userEntity.getRole().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}