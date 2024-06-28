package com.kosta.legolego.review.controller;

import com.kosta.legolego.review.dto.PreTripBoardDetailDto;
import com.kosta.legolego.review.dto.PreTripBoardDto;
import com.kosta.legolego.review.service.PreTripBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//@RequestMapping("/pre-trip")
public class PreTripBoardController {

    @Autowired
    PreTripBoardService preTripBoardService;

    // 게시판 조회
    @GetMapping("/pre-trip")
    public ResponseEntity<List<PreTripBoardDto>> getAllPreTripBoard() {
        List<PreTripBoardDto> preTripBoards = preTripBoardService.getAllPreTripBoard();
        return ResponseEntity.status(HttpStatus.OK).body(preTripBoards);
    }

    // 게시판 상세 조회
    @GetMapping("/pre-trip/{board_num}")
    public ResponseEntity<PreTripBoardDetailDto> getPreTripBoardDetail(@PathVariable("board_num") Long boardNum){
        PreTripBoardDetailDto preTripBoardDto = preTripBoardService.getPreTripBoardDetails(boardNum);
        return  ResponseEntity.status(HttpStatus.OK).body(preTripBoardDto);
    }

    // only admin - 게시글 삭제
}
