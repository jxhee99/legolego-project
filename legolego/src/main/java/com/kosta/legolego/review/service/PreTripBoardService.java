package com.kosta.legolego.review.service;

import com.kosta.legolego.products.dto.ProductDetailDto;
import com.kosta.legolego.products.service.ProductService;
import com.kosta.legolego.review.dto.PreTripBoardDetailDto;
import com.kosta.legolego.review.dto.PreTripBoardDto;
import com.kosta.legolego.review.dto.ReviewDto;
import com.kosta.legolego.review.entity.PreTripBoard;
import com.kosta.legolego.review.repository.PreTripBoardRepository;
import com.kosta.legolego.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreTripBoardService {

    @Autowired
    PreTripBoardRepository preTripBoardRepository;

    @Autowired
    ReviewRepository reviewRepository;


    @Autowired
    ProductService productService;

    // 게시글 전체 조회
    public List<PreTripBoardDto> getAllPreTripBoard() {
        return preTripBoardRepository.findAll().stream()
                .map(PreTripBoardDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회
    public PreTripBoardDetailDto getPreTripBoardDetails(Long boardNum) {
        PreTripBoard preTripBoard = preTripBoardRepository.findById(boardNum)
                .orElseThrow(() -> new RuntimeException("해당 게시판 번호를 찾을 수 없습니다."));

        ProductDetailDto productDetailDto = productService.getProductByDetailId(preTripBoard.getProduct().getProductNum());

        List<ReviewDto> reviwes = reviewRepository.findByPreTripBoard(preTripBoard)
                .stream().map(ReviewDto::fromEntity).collect(Collectors.toList());

        return PreTripBoardDetailDto.fromEntity(preTripBoard, productDetailDto, reviwes);
    }
}
