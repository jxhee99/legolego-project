package com.kosta.legolego.security;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.id = user.getUserNum();
        this.email = user.getUserEmail();
        this.password = user.getUserPw();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    public CustomUserDetails(Admin admin) {
        this.id = admin.getAdminNum();
        this.email = admin.getAdminEmail();
        this.password = admin.getAdminPw();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public CustomUserDetails(Partner partner) {
        this.id = partner.getPartnerNum();
        this.email = partner.getPartnerEmail();
        this.password = partner.getPartnerPw();
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_PARTNER"));
    }

    public String getRole() {
        return authorities.iterator().next().getAuthority();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
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
