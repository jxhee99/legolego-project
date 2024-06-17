package com.kosta.legolego.user.service;

import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserSecurityService  implements UserDetailsService {

    private final UserRepository userRepository;


@Override
public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
    Optional<User> _siteUser = this.userRepository.findByUserEmail(userEmail);
    if (_siteUser.isEmpty()) {
        throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
    }
    User user = _siteUser.get();
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    return new org.springframework.security.core.userdetails.User(user.getUserEmail(), user.getUserPw(), authorities);
}
}