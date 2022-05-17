package com.clone.instagram.config.auth;

import com.clone.instagram.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;
    private Map<String, Object> attributes = new HashMap<>();

    public PrincipalDetails(User user) {
        this.user = user;
    }

    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // 사용자에세 부여된 권한 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    // 사용자를 인증하는데 사용된 암호를 반환
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 사용자를 인증하는데 사용된 사용자 이름을 반환
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // 사용자의 계정이 만료되었는지 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 사용자가 잠겨있는지 여부
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 사용자의 자격 증명(암호)이 만료되었는지 여부
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 사용자가 활성화되었는지 여부
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }
}
