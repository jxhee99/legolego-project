package com.kosta.legolego.security;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.admin.repository.AdminRepository;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.partner.repository.PartnerRepository;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByUserEmail(email);
        if (user != null) {
            return new CustomUserDetails(user);
        }

        Admin admin = adminRepository.findByAdminEmail(email);
        if (admin != null) {
            return new CustomUserDetails(admin);
        }

        Partner partner = partnerRepository.findByPartnerEmail(email);
        if (partner != null) {
            return new CustomUserDetails(partner);
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
