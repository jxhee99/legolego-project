package com.kosta.legolego.products.service;

import com.kosta.legolego.orders.repository.OrderRepository;
import com.kosta.legolego.products.dto.ProductDto;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.products.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductFilterService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    // 모집 임박 상품 조회
    public List<ProductDto> getRecruitmentCloseProduct() {

        LocalDateTime currentTimestamp = LocalDateTime.now();

        // 공통 필터링 : 여행 출발 날짜 지나지 않은 상품들
        List<Product> products = productRepository.findBeforeBoardingDate(currentTimestamp);
        products.stream().filter(product -> {long paymentCount = orderRepository.countByProductAndPaymentStatus(product, true);
            double recruitmentRate = (double) product.getNecessaryPeople() * 0.8;
            return paymentCount >= recruitmentRate && recruitmentRate < 1;
        }).collect(Collectors.toList());

        return products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }


    // 마감 임박 상품 조회
    public List<ProductDto> getSortByDeadlineDescProduct() {

        LocalDateTime currentTimestamp = LocalDateTime.now();

        // 공통 필터링 : 여행 출발 날짜 지나지 않은 상품들
        List<Product> products = productRepository.findBeforeBoardingDate(currentTimestamp);
        products = productRepository.findRecruitmentDeadlineAsc().stream()
                    .filter(products::contains).collect(Collectors.toList());

        return products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 모집 확정 상품 조회
    public List<ProductDto> getRecruitmentConfirmProduct() {

        LocalDateTime currentTimestamp = LocalDateTime.now();

        // 공통 필터링 : 여행 출발 날짜 지나지 않은 상품들
        List<Product> products = productRepository.findBeforeBoardingDate(currentTimestamp);
        products = productRepository.findRecruitmentConfirmed().stream()
                .filter(products::contains).collect(Collectors.toList());

        return products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 최신 등록 상품들
    public List<ProductDto> getSortByRegDateDescProduct() {
        LocalDateTime currentTimestamp = LocalDateTime.now();

        // 공통 필터링 : 여행 출발 날짜 지나지 않은 상품들
        List<Product> products = productRepository.findBeforeBoardingDate(currentTimestamp);
        products = productRepository.findLatestProducts().stream()
                .filter(products::contains).collect(Collectors.toList());

        return products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 주문 내역 많은 상품들(인기순)
    public List<ProductDto> getSortByPoplarProduct() {
        LocalDateTime currentTimestamp = LocalDateTime.now();

        // 공통 필터링 : 여행 출발 날짜 지나지 않은 상품들
        List<Product> products = productRepository.findBeforeBoardingDate(currentTimestamp);
        products = productRepository.findPopularProducts().stream()
                .filter(products::contains).collect(Collectors.toList());

        return products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 가격 높은 상품 순서
    public List<ProductDto> getSortByPriceDescProduct() {
        LocalDateTime currentTimestamp = LocalDateTime.now();

        // 공통 필터링 : 여행 출발 날짜 지나지 않은 상품들
        List<Product> products = productRepository.findBeforeBoardingDate(currentTimestamp);
        products = productRepository.findPriceDesc().stream()
                .filter(products::contains).collect(Collectors.toList());

        return products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 가격 낮은 상품 순서
    public List<ProductDto> getSortByPriceAscProduct() {
        LocalDateTime currentTimestamp = LocalDateTime.now();

        // 공통 필터링 : 여행 출발 날짜 지나지 않은 상품들
        List<Product> products = productRepository.findBeforeBoardingDate(currentTimestamp);
        products = productRepository.findPriceAsc().stream()
                .filter(products::contains).collect(Collectors.toList());

        return products.stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }
}
