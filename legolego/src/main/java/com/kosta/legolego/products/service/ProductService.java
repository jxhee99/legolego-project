package com.kosta.legolego.products.service;

import com.kosta.legolego.admin.repository.AdminRepository;
import com.kosta.legolego.diypackage.dto.DiyAirlineDTO;
import com.kosta.legolego.diypackage.dto.DiyDetailCourseDTO;
import com.kosta.legolego.diypackage.dto.DiyRouteDTO;
import com.kosta.legolego.diypackage.entity.DiyList;
import com.kosta.legolego.diypackage.repository.DetailCourseRepository;
import com.kosta.legolego.diypackage.repository.DiyListRepository;
import com.kosta.legolego.image.repository.ImageRepository;
import com.kosta.legolego.image.service.ImageService;
import com.kosta.legolego.products.dto.ProductDetailDto;
import com.kosta.legolego.products.dto.ProductDetailInfo;
import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductRepository;
import com.kosta.legolego.review.entity.PreTripBoard;
import com.kosta.legolego.review.repository.PreTripBoardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DiyListRepository diyListRepository;

    @Autowired
    DetailCourseRepository detailCourseRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    PreTripBoardRepository preTripBoardRepository;

    @Autowired
    ImageService imageService;


//  상품 전체 조회
    public List<ProductDto> getAllProducts(){
        return productRepository.findAll().stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    //   상품 상세 조회
    public ProductDetailDto getProductByDetailId(Long productNum){
        Product product = productRepository.findById(productNum)
                .orElseThrow(()->new RuntimeException("상품을 찾을 수 없습니다."));

        DiyList diyList = diyListRepository.findByProductNum(productNum)
                .orElseThrow(()->new RuntimeException("Diy 리스트를 찾을 수 없습니다."));

        // DIY 패키지의 항공사 정보를 DTO로 변환
        DiyAirlineDTO diyAirlineDTO = DiyAirlineDTO.toAirlineDTO(diyList.getDiyPackage().getAirline());
        // DIY 패키지의 경로 정보를 DTO로 변환
        DiyRouteDTO diyRouteDTO = DiyRouteDTO.toRouteDTO(diyList.getDiyPackage().getRoute());
        // 경로에 따라 상세 코스 리스트를 찾아와 DTO로 변환
        List<DiyDetailCourseDTO> diyDetailCourseDTOList = DiyDetailCourseDTO
                .toDetailCourseDTOList(detailCourseRepository
                        .findByRoute(diyList.getDiyPackage().getRoute()));

        // 각 상세 코스에 대해 이미지 가져오기
        for (DiyDetailCourseDTO diyDetailCourseDTO : diyDetailCourseDTOList) {
            Long detailCourseNum = diyDetailCourseDTO.getDetailCourseNum();
            List<String> imageUrls = imageService.getImagesByDetailCourse(detailCourseNum);
            diyDetailCourseDTO.setFileUrls(imageUrls);
        }


        ProductDetailInfo info = new ProductDetailInfo(diyList, diyAirlineDTO, diyRouteDTO, diyDetailCourseDTOList);

        return ProductDetailDto.fromInfo(product, info);

    }

//    상품 검색
    public List<ProductDto> searchProducts(String keyword){
        return productRepository.findByProductNameContaining(keyword).stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

//    관리자용 상품 전체 조회
public List<ProductDto> getAllProductsForAdmin(){
        return productRepository.findAll().stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
}

//   only admin - 상품 수정
public ProductDto updateProduct(Long productNum, ProductDto productDto, Long adminNum){
    // 관리자인지 확인
    if(!adminRepository.existsById(adminNum)) {
//        throw new IllegalArgumentException("상품을 수정 할 수 있는 권한이 없습니다.");
        throw new IllegalArgumentException("상품을 수정 할 수 있는 권한이 없습니다.");
    }

    Product product = productRepository.findById(productNum)
            .orElseThrow(()-> new IllegalArgumentException("상품 수정 실패! " + "대상 상품이 없습니다."));
    product.patch(productDto);

    Product updatedProduct = productRepository.save(product);

    return ProductDto.fromEntity(updatedProduct);
}

//    only admin - 상품 삭제
    public void deleteProduct(Long productNum, Long adminNum){
        if(!adminRepository.existsById(adminNum)){
            throw new IllegalArgumentException("상품을 삭제 할 수 있는 권한이 없습니다.");
        }

      if(!productRepository.existsById(productNum)){
          throw new IllegalArgumentException("상품 삭제 실패! 대상 상품이 없습니다.");
      }
      productRepository.deleteById(productNum);
    }

    // 지난 여행 게시판으로 이동
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // 자정마다 실행 -> 추후 변경 예정
    public void moveConfirmedProductsToPreTripBoard() {
//        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        LocalDateTime currentTimestamp = LocalDateTime.now();
        log.info("Current timestamp: {}", currentTimestamp);

        List<Product> products = productRepository.findByConfirmedAndBoardingDateBefore(currentTimestamp);
        log.info("Found {} products to move to pre-trip board", products.size());

        for(Product product : products) {
            log.info("Moving product {} to pre-trip board", product.getProductNum());

            PreTripBoard preTripBoard = new PreTripBoard();
            product.setReviewAble(true); // 리뷰 작성 권한을 부여
            preTripBoard.setProduct(product);

            preTripBoardRepository.save(preTripBoard);
        }
    }

}