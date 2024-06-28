package com.kosta.legolego.image.controller;

import com.kosta.legolego.image.entity.Image;
import com.kosta.legolego.image.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    ImageService imageService;

    @PostMapping("/upload-url")
    public ResponseEntity<String> uploadImageUrl(@RequestParam("image_url") List<String> imageUrls, @RequestParam("detailCourse_num") Long detailCourseNum){
        imageService.saveImageUrls(imageUrls, detailCourseNum);
        return ResponseEntity.ok("이미지 url 저장 성공");
    }

    @GetMapping("/detail-course/{detailCourseNum}")
    public ResponseEntity<List<String>> getIamgesByDetailCourse(@PathVariable Long detailCourseNum) {
        List<String> imageUrls = imageService.getImagesByDetailCourse(detailCourseNum);
        return ResponseEntity.ok(imageUrls);
    }

}
