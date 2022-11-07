package baedalmate.baedalmate.security.user;

import baedalmate.baedalmate.security.oauth2.soical.SocialType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Builder
@Getter
public class OAuth2UserDetails implements UserDetails {
    private Long id;
    private SocialType socialType;
    private String socialId;
    private String username;
    private String image;
    private Set<GrantedAuthority> authorities;

    public SocialType getSocialType() {
        return socialType;
    }

    public String getSocialId() {
        return socialId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setRoles(String... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>(roles.length);

        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        this.authorities = Set.copyOf(authorities);
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}