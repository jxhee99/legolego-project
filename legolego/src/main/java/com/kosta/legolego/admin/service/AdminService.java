package com.kosta.legolego.admin.service;

import com.kosta.legolego.admin.dto.AdminProfileDto;
import com.kosta.legolego.admin.dto.MemberDto;
import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.admin.repository.AdminRepository;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.partner.repository.PartnerRepository;
import com.kosta.legolego.user.dto.UpdatePasswordDto;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PartnerRepository partnerRepository;

    // 프로필 조회
    public AdminProfileDto getProfile(Long adminNum) {
        Admin admin = adminRepository.findById(adminNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 관리자 ID입니다."));

        return new AdminProfileDto(
                admin.getAdminNum(),
                admin.getAdminEmail(),
                admin.getAdminName()
        );
    }

    // 프로필 변경
    public boolean updateProfile(Long adminNum, AdminProfileDto dto) {
        Admin admin = adminRepository.findById(adminNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 관리자 ID입니다."));

        if (dto.getAdminName() != null && !dto.getAdminName().isEmpty()) {
            admin.setAdminName(dto.getAdminName());
        }

        adminRepository.save(admin);
        return true;
    }

    // 비밀번호 변경
    public boolean updatePassword(Long adminNum, UpdatePasswordDto dto) {
        Admin admin = adminRepository.findById(adminNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 관리자 ID입니다."));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), admin.getAdminPw())) {
            return false;
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        admin.setAdminPw(passwordEncoder.encode(dto.getNewPassword()));
        adminRepository.save(admin);
        return true;
    }

    // 전체 회원 조회
    public List<MemberDto> getAllMembers() {
        List<MemberDto> members = new ArrayList<>();

        List<User> users = userRepository.findAll();
        for (User user : users) {
            members.add(new MemberDto(
                    user.getUserNum(),
                    user.getUserEmail(),
                    user.getUserName(),
                    user.getUserNickname(),
                    null,
                    user.getUserPhone()
            ));
        }

        List<Partner> partners = partnerRepository.findAll();
        for (Partner partner : partners) {
            members.add(new MemberDto(
                    partner.getPartnerNum(),
                    partner.getPartnerEmail(),
                    null,
                    null,
                    partner.getCompanyName(),
                    partner.getPartnerPhone()
            ));
        }

        return members;
    }
}
