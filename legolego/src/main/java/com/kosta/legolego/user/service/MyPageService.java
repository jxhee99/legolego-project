package com.kosta.legolego.user.service;

import com.kosta.legolego.diypackage.entity.DiyLikeEntity;
import com.kosta.legolego.diypackage.entity.DiyPackage;
import com.kosta.legolego.diypackage.repository.DiyLikeRepository;
import com.kosta.legolego.user.dto.MyPageDto;
import com.kosta.legolego.user.dto.MyProfileDto;
import com.kosta.legolego.user.dto.UpdatePasswordDto;
import com.kosta.legolego.user.entity.User;
import com.kosta.legolego.user.repository.MyPageRepository;
import com.kosta.legolego.user.repository.UserRepository;
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
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 내가 쓴 글 리스트 조회
    public List<MyPageDto> getPackagesByUserNum(Long userNum) {
        List<DiyPackage> diyPackages = myPageRepository.findByUserUserNum(userNum);
        return diyPackages.stream()
                        .map(MyPageDto::new)
                        .collect(Collectors.toList());
    }

    // 응원하기 버튼 누른 게시물 리스트 조회
    public List<MyPageDto> getLikedPackagesByUserNum(Long userNum) {
        List<DiyLikeEntity> likes = diyLikeRepository.findByUserNum(userNum);
        return likes.stream()
                    .map(like -> new MyPageDto(like.getDiy()))
                    .collect(Collectors.toList());
    }

    // 내가 쓴 글 목록 중 응원하기 조건 충족한 리스트만 조회
//    public List<MyPageDto> getUserLikedPackages(Long userNum, Integer packageLikedNum) {
//        log.info("Fetching liked packages for userNum: {} with packageLikedNum >= {}", userNum, packageLikedNum);
//        List<DiyList> diyLists = myPageRepository.findByDiyPackageUserUserNumAndDiyPackagePackageLikedNumGreaterThanEqual(userNum, packageLikedNum);
//        return diyLists.stream().map(MyPageDto::new).collect(Collectors.toList());
//    }

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
    public void deleteUser(Long userNum) {
        User user = userRepository.findById(userNum)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 사용자 ID입니다."));

        userRepository.delete(user);
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
