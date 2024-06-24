package com.kosta.legolego.image.service;

import com.kosta.legolego.diypackage.entity.DetailCourseEntity;
import com.kosta.legolego.diypackage.repository.DetailCourseRepository;
import com.kosta.legolego.image.entity.Image;
import com.kosta.legolego.image.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    DetailCourseRepository detailCourseRepository;

    // 프론트에서 받아서 이미지 url 저장
    public void saveImageUrls(List<String> imageUrls, Long detailCourseNum){

        DetailCourseEntity detailCourse = detailCourseRepository.findById(detailCourseNum)
                .orElseThrow(()-> new IllegalArgumentException("디테일 코스 번호를 찾을 수 없습니다."));

        for (int i=0; i<imageUrls.size(); i++) {
            Image image = Image.builder()
                    .imageUrl(imageUrls.get(i))
                    .detailCourse(detailCourse)
                    .build();

            imageRepository.save(image);
            }
        }


    // detailCourse 엔티티와 연관된 image 엔티티를 조회 후 반환 -> 해당 코스에 속한 모든 이미지 반환
    public List<String> getImagesByDetailCourse(Long detailCourseNum){
        DetailCourseEntity detailCourse = detailCourseRepository.findById(detailCourseNum)
                .orElseThrow(()-> new IllegalArgumentException("디테일 코스 번호를 찾을 수 없습니다."));

        List<Image> images = imageRepository.findByDetailCourse(detailCourse);

        return images.stream().map(Image::getImageUrl).toList();
    }

}
