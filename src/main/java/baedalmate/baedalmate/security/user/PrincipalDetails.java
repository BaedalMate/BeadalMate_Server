package baedalmate.baedalmate.security.user;

import baedalmate.baedalmate.user.domain.User;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Schema(hidden = true)
public class PrincipalDetails implements UserDetails, OAuth2User {
    private static final long serialVersionUID = 1L;
    private User user;
    private Map<String, Object> attributes;

    // OAuth2.0 로그인시 사용
    public PrincipalDetails(User user) {
        this.user = user;
    }

    public static PrincipalDetails create(User user) {
        return new PrincipalDetails(user);
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public String getUsername() {
        return user.getNickname();
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<GrantedAuthority>();
        collect.add(() -> {
            return "ROLE_" + user.getRole();
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return null;
    }

    // 리소스 서버로 부터 받는 회원정보
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // User의 PrimaryKey
    @Override
    public String getName() {
        return user.getId() + "";
    }
}
