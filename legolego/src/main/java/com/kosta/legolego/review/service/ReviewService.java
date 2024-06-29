package com.kosta.legolego.review.service;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.admin.repository.AdminRepository;
import com.kosta.legolego.orders.entity.Order;
import com.kosta.legolego.orders.repository.OrderRepository;
import com.kosta.legolego.products.entity.Product;
import com.kosta.legolego.review.dto.ReviewDto;
import com.kosta.legolego.review.entity.Review;
import com.kosta.legolego.review.repository.PreTripBoardRepository;
import com.kosta.legolego.review.repository.ReviewRepository;
import com.kosta.legolego.user.entity.User;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Slf4j
@Service
public class ReviewService {

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PreTripBoardRepository preTripBoardRepository;

    @Autowired
    AdminRepository adminRepository;

    // 리뷰 생성
    @Transactional
    public ReviewDto createReview(Long orderNum, ReviewDto reviewDto) {
        try {
            Order order = orderRepository.findById(orderNum)
                    .orElseThrow(() -> new RuntimeException("주문번호를 찾을 수 없습니다."));
            log.info("Order 정보 : {}", orderNum);

            Product product = order.getProduct();

            if(!order.getPaymentStatus()) {
                throw new IllegalArgumentException("결제가 완료되지 않은 주문입니다.");
            }
            log.info("결제 완료된 주문 : {}", orderNum);

            if (!product.getReviewAble()) {
                throw new IllegalArgumentException("리뷰 작성 비활성화 상품 입니다.");
            }
            log.info("Product reviewable : {}", product.getProductNum());

            // 주문한 상품이 지난 여행 게시판에 있는지 확인
            if (!preTripBoardRepository.existsByProduct(product)) {
                throw new IllegalArgumentException("상품이 지난여행 게시판에 존재하지 않습니다.");
            }
            log.info("상품이 지난 여행 게시판에 존재 : {}", product.getProductNum());

            Review review = ReviewDto.toEntity(reviewDto);
            review.setOrder(order);

            review.setPreTripBoard(preTripBoardRepository.findByProduct(order.getProduct()));

            if (review.getCreatedDate() == null) {
                review.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            }
            Review savedReview = reviewRepository.save(review);
            log.info("Review 저장 : {}", savedReview.getReviewNum());

            // Order 엔티티의 review 필드 업데이트
            order.setReview(savedReview);
            orderRepository.save(order);
            log.info("Order 업데이트 : {}", order.getOrderNum());

            return ReviewDto.fromEntity(savedReview);

        } catch (Exception e) {
            log.error("리뷰 생성 오류", e);
            throw e;
        }
}

    // 리뷰 수정
    @Transactional
    public ReviewDto updateReview(Long reviewNum, ReviewDto reviewDto, Long userNum){
        try {
            Review review = reviewRepository.findById(reviewNum)
                    .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
            log.info("review 정보  : {}", reviewNum);

            Order order = review.getOrder();
            User user = order.getUser();

            if(!preTripBoardRepository.existsByProduct(order.getProduct())) {
                throw new IllegalArgumentException("리뷰가 지난 여행 게시판에 존재하지 않습니다.");
            }
            log.info("지난 여행 게시판에 존재하는 리뷰 : {}", review.getPreTripBoard().getBoardNum());

            if(user.getUserNum() != userNum) {
                throw new SecurityException("자신의 리뷰만 수정할 수 있습니다.");
            }
            log.info("리뷰 작성자  : {}", userNum);

            // 리뷰 내용 수정
            review.patch(reviewDto);
            review.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            Review updatedReview = reviewRepository.save(review);
            log.info("수정된 리뷰 : {}", updatedReview.getReviewNum());

            return ReviewDto.fromEntity(updatedReview);

        } catch (Exception e) {
            log.error("리뷰 수정 오류", e);
            throw e;
        }
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewNum){
        try {
            Review review = reviewRepository.findById(reviewNum)
                    .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
            log.info("review 정보 확인 : {}", reviewNum);

            Order order = review.getOrder();
//            User user = order.getUser();

            if (!preTripBoardRepository.existsByProduct(order.getProduct())) {
                throw new IllegalArgumentException("리뷰가 지난 여행 게시판에 존재하지 않습니다.");
            }
            log.info("지난 여행 게시판에 존재하는 리뷰 확인 : {}", review.getPreTripBoard().getBoardNum());

//            if (user.getUserNum() != userNum) {
//                throw new SecurityException("자신의 리뷰만 삭제할 수 있습니다.");
//            }
//            log.info("리뷰 작성자 확인 : {}", userNum);

            // order 엔티티 외래키 제약조건으로 발생하는 에러 -> review 필드를 null로 먼저 설정 후 삭제
            order.setReview(null);
            orderRepository.save(order);
            log.info("Order 업데이트 확인: {}", order.getOrderNum());

            reviewRepository.delete(review);
        } catch (Exception e) {
            log.error("리뷰 삭제 오류", e);
            throw e;
        }
    }

    // only admin - 리뷰 삭제
    @Transactional
    public void deleteReviewForAdmin(Long reveiwNum) {
        try {
            Review review = reviewRepository.findById(reveiwNum)
                    .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
            log.info("리뷰 정보 : {}", reveiwNum);

//            Admin admin = adminRepository.findByAdminNum(adminNum);
//
//            if(!admin.getAdminNum().equals(adminNum)) {
//                throw new SecurityException("관리자만 삭제할 수 있습니다.");
//            }
//            log.info("관리자 확인 : {}", admin.getAdminNum());

            Order order = review.getOrder();
            order.setReview(null);
            orderRepository.save(order);

            reviewRepository.delete(review);
        } catch (Exception e) {
            log.error("리뷰 삭제 오류", e);
            throw e;
        }
    }
}
