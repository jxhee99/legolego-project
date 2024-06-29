package com.kosta.legolego.review.controller;

import com.kosta.legolego.review.dto.PreTripBoardDetailDto;
import com.kosta.legolego.review.dto.PreTripBoardDto;
import com.kosta.legolego.review.service.PreTripBoardService;
import com.kosta.legolego.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
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
    @DeleteMapping("/admin/pre-trip/{board_num}/delete")
    public ResponseEntity<Void> deletePreTripBoard(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable("board_num") Long boardNum) {
        if (userDetails == null || !userDetails.getRole().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        preTripBoardService.deletePreTripBoard(boardNum);
        return ResponseEntity.noContent().build();
    }
}
