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
import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.orders.repository.OrderRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.lettuce.core.GeoArgs.Sort.desc;


@Slf4j
@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DiyListRepository diyListRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    DetailCourseRepository detailCourseRepository;

    @Autowired
    PreTripBoardRepository preTripBoardRepository;

    @Autowired
    ImageService imageService;


//  상품 전체 조회 : 출발 날짜 지난 상품 제외 및 다양한 필터링 옵션 추가
    public List<ProductDto> getAllProducts(
            Optional<Boolean> isRecruitmentClose,
            Optional<Boolean> isRecruitmentConfirmed,
            Optional<Boolean> sortByDeadlineDesc,
            Optional<Boolean> sortByRegDateDesc,
            Optional<Boolean> sortByPoplar,
            Optional<Boolean> sortByPriceDesc,
            Optional<Boolean> sortByPriceAsc){


        LocalDateTime currentTimestamp = LocalDateTime.now();
        log.info("Current timestamp: {}", currentTimestamp);

        // 공통 필터링 : 여행 출발 날짜 지나지 않은 상품들
        List<Product> products = productRepository.findBeforeBoardingDate(currentTimestamp);

        // 추가 필터링 및 정렬 조건이 없는 경우 공통 필터링된 상품 목록 반환
        if (!isRecruitmentClose.isPresent() && !isRecruitmentConfirmed.isPresent() &&
                !sortByDeadlineDesc.isPresent() && !sortByRegDateDesc.isPresent() &&
                !sortByPoplar.isPresent() && !sortByPriceDesc.isPresent() && !sortByPriceAsc.isPresent()) {
            return products.stream()
                    .map(ProductDto::fromEntity)
                    .collect(Collectors.toList());
        }

        // 모집 임박 상품들
        if(isRecruitmentClose.isPresent() && isRecruitmentClose.get()) {
            products = products.stream().filter(product -> {long paymentCount = orderRepository.countByProductAndPaymentStatus(product, true);
                                                            double recruitmentRate = (double) product.getNecessaryPeople() * 0.8;
                                                            return paymentCount >= recruitmentRate;
            }).collect(Collectors.toList());
        }

        // 마감 임박 상품들(recruitment_deadline 내림차순)
        if(sortByDeadlineDesc.isPresent() && sortByDeadlineDesc.get()){
            products = productRepository.findRecruitmentDeadlineDesc().stream()
                    .filter(products::contains).collect(Collectors.toList());
        }

        // 모집 확정 상품들(recruitment_confirmed = true)
        if(isRecruitmentConfirmed.isPresent() && isRecruitmentConfirmed.get()) {
           products = productRepository.findRecruitmentConfirmed().stream()
                   .filter(products::contains).collect(Collectors.toList());

        }

        // 최신 등록 상품들(reg_date 내림차순)
        if(sortByRegDateDesc.isPresent() && sortByRegDateDesc.get()) {
            products =  productRepository.findLatestProducts().stream()
                    .filter(products::contains).collect(Collectors.toList());
        }

        // 주문 내역 많은 상품들(인기순 : count(payment_status = true) 내림차순)
        if(sortByPoplar.isPresent() && sortByPoplar.get()) {
            products = productRepository.findPopularProducts().stream()
                    .filter(products::contains).collect(Collectors.toList());
        }

        // 가격 높은 상품 순서(price 내림차순)
        if(sortByPriceDesc.isPresent() && sortByPriceDesc.get()){
            products = productRepository.findPriceDesc().stream()
                    .filter(products::contains).collect(Collectors.toList());
        }
        // 가격 낮은 상품 순서(price 오름차순)
        if( sortByPriceAsc.isPresent() && sortByPriceAsc.get()){
            products = productRepository.findPriceAsc().stream()
                    .filter(products::contains).collect(Collectors.toList());
        }

        return products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }


    //   상품 상세 조회
    @Transactional
    public ProductDetailDto getProductByDetailId(Long productNum){

        Product product = productRepository.findById(productNum)
                .orElseThrow(()->new RuntimeException("상품을 찾을 수 없습니다."));

        DiyList diyList = diyListRepository.findByProductNum(productNum)
                .orElseThrow(()->new RuntimeException("Diy 리스트를 찾을 수 없습니다."));

        // 조회수 증가
        product.setProductViewNum(product.getProductViewNum() + 1);
        productRepository.save(product);
        log.info("누적 조회수 : {}", product.getProductViewNum());

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

        // 상품 구매 인원수
        long orderCount = orderRepository.countByProductAndPaymentStatus(product, true);

        ProductDetailInfo info = new ProductDetailInfo(diyList, diyAirlineDTO, diyRouteDTO, diyDetailCourseDTOList);

        return ProductDetailDto.fromInfo(product, info, (int)orderCount);

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
public ProductDto updateProduct(Long productNum, ProductDto productDto){
    Product product = productRepository.findById(productNum)
            .orElseThrow(()-> new IllegalArgumentException("상품 수정 실패! " + "대상 상품이 없습니다."));
    product.patch(productDto);

    Product updatedProduct = productRepository.save(product);

    return ProductDto.fromEntity(updatedProduct);
}

//    only admin - 상품 삭제
    public void deleteProduct(Long productNum){
      if(!productRepository.existsById(productNum)){
          throw new IllegalArgumentException("상품 삭제 실패! 대상 상품이 없습니다.");
      }
      productRepository.deleteById(productNum);
    }

    // 지난 여행 게시판으로 이동
    @Transactional
//    @Scheduled(cron = "0 0 0 * * ?") // 자정마다 실행 -> 추후 변경 예정
    @Scheduled(fixedRate = 600000)
    public void moveConfirmedProductsToPreTripBoard() {
//        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        LocalDateTime currentTimestamp = LocalDateTime.now();
        log.info("Current timestamp: {}", currentTimestamp);

        List<Product> products = productRepository.findByConfirmedAndReviewUnableAndBoardingDateBefore(currentTimestamp);
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