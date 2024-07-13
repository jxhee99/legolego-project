package com.kosta.legolego.user.service;

import com.kosta.legolego.diypackage.entity.DiyLikeEntity;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.repository.DiyLikeRepository;
import com.kosta.legolego.diypackage.repository.DiyListRepository;
import com.kosta.legolego.user.dto.MyPageDto;
import com.kosta.legolego.user.dto.MyProfileDto;
import com.kosta.legolego.user.dto.UpdatePasswordDto;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.MyPageRepository;
import com.kosta.legolego.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MyPageService {

    @Autowired
    private MyPageRepository myPageRepository;
    @Autowired
    private DiyLikeRepository diyLikeRepository;
    @Autowired
    private DiyListRepository diyListRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 내가 쓴 글 리스트 조회
    public List<MyPageDto> getPackagesByUserNum(Long userNum) {
        List<DiyPackage> diyPackages = myPageRepository.findByUserUserNum(userNum);
        return diyPackages.stream()
                .map(diyPackage -> {
                    DiyList diyList = diyListRepository.findByDiyPackage(diyPackage).stream().findFirst().orElse(null);
                    return new MyPageDto(diyPackage, diyList);
                })
                .collect(Collectors.toList());
    }

    // 응원하기 버튼 누른 게시물 리스트 조회
    public List<MyPageDto> getLikedPackagesByUserNum(Long userNum) {
        List<DiyLikeEntity> likes = diyLikeRepository.findByUserNum(userNum);
        return likes.stream()
                .map(like -> {
                    DiyList diyList = diyListRepository.findByDiyPackage(like.getDiy()).stream().findFirst().orElse(null);
                    return new MyPageDto(like.getDiy(), diyList);
                })
                .collect(Collectors.toList());
    }

    // 프로필 조회
    public MyProfileDto getProfile(Long userNum) {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID입니다."));

        return new MyProfileDto(
                user.getUserNum(),
                user.getUserNickname(),
                user.getUserPhone(),
                user.getUserEmail(),
                user.getUserName()
        );
    }

    // 프로필 변경
    public boolean updateProfile(Long userNum, MyProfileDto myProfileDto) {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID입니다."));

        if (myProfileDto.getUserNickname() != null && !myProfileDto.getUserNickname().isEmpty()) {
            if (userRepository.existsByUserNickname(myProfileDto.getUserNickname())) {
                return false;  // 닉네임 중복 처리
            }
            user.setUserNickname(myProfileDto.getUserNickname());
        }

        if (myProfileDto.getUserPhone() != null && !myProfileDto.getUserPhone().isEmpty()) {
            user.setUserPhone(myProfileDto.getUserPhone());
        }

        userRepository.save(user);
        return true;
    }

    // 비밀번호 변경
    public boolean updatePassword(Long userNum, UpdatePasswordDto updatePasswordDto) {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID입니다."));

        if (!passwordEncoder.matches(updatePasswordDto.getCurrentPassword(), user.getUserPw())) {
            return false;
        }

        if (!updatePasswordDto.getNewPassword().equals(updatePasswordDto.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 일치하지 않습니다.");
        }

        user.setUserPw(passwordEncoder.encode(updatePasswordDto.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(Long userNum) {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID입니다."));

        user.setUserStatus(User.UserStatus.withdrawal);
        userRepository.save(user);
    }


    // 프로필 이미지 업데이트 - 일단 로컬에 저장
    public void updateProfileImage(Long userNum, MultipartFile image) throws IOException {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        // 이미지 저장 로직
        String imagePath = saveImageLocally(image);

        user.setProfileImage(imagePath);
        userRepository.save(user);
    }

    private String saveImageLocally(MultipartFile image) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
        Path path = Paths.get("local_images/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, image.getBytes());
        return path.toString();
    }

    // 프로필 이미지 조회 (기본 이미지)
    public String getProfileImage(Long userNum) {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        return user.getProfileImage() != null ? user.getProfileImage() : "/path/to/default/image.jpg";
    }

}
