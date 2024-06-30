package com.kosta.legolego.partner.service;

import com.kosta.legolego.partner.dto.PartnerProfileDto;
import com.kosta.legolego.user.dto.UpdatePasswordDto;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.partner.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PartnerService {

    @Autowired
    private PartnerRepository partnerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 프로필 조회
    public PartnerProfileDto getProfile(Long partnerNum) {
        Partner partner = partnerRepository.findById(partnerNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 파트너 ID입니다."));

        return new PartnerProfileDto(
                partner.getPartnerNum(),
                partner.getPartnerEmail(),
                partner.getCompanyName(),
                partner.getPartnerPhone()
        );
    }

    // 프로필 변경
    public boolean updateProfile(Long partnerNum, PartnerProfileDto dto) {
        Partner partner = partnerRepository.findById(partnerNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 파트너 ID입니다."));

        if (dto.getPartnerPhone() != null && !dto.getPartnerPhone().isEmpty()) {
            partner.setPartnerPhone(dto.getPartnerPhone());
        }

        partnerRepository.save(partner);
        return true;
    }

    // 비밀번호 변경
    public boolean updatePassword(Long partnerNum, UpdatePasswordDto dto) {
        Partner partner = partnerRepository.findById(partnerNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 파트너 ID입니다."));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), partner.getPartnerPw())) {
            return false;
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        partner.setPartnerPw(passwordEncoder.encode(dto.getNewPassword()));
        partnerRepository.save(partner);
        return true;
    }

    // 탈퇴
    public void deleteUser(Long partnerNum) {
        Partner partner = partnerRepository.findById(partnerNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 파트너 ID입니다."));

        partnerRepository.delete(partner);
    }

}
