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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    // 프로필 이미지 업데이트 - 로컬에 저장
    public void updateProfileImage(Long adminNum, MultipartFile image) throws IOException {
        Admin admin = adminRepository.findById(adminNum)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // 이미지 저장 로직
        String imagePath = saveImageLocally(image);

        admin.setProfileImage(imagePath);
        adminRepository.save(admin);
    }

    private String saveImageLocally(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path path = Paths.get("local_images/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
        return path.toString();
    }

    // 프로필 이미지 조회 (기본 이미지)
    public String getProfileImage(Long adminNum) {
        Admin admin = adminRepository.findById(adminNum)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        return admin.getProfileImage() != null ? admin.getProfileImage() : "/path/to/default/image.jpg";
    }

}
