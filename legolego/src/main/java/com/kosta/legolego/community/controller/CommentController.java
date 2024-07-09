package com.kosta.legolego.community.controller;

import com.kosta.legolego.community.dto.CommentDto;
import com.kosta.legolego.community.service.CommentService;
import com.kosta.legolego.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts/{post_num}/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 전체 댓글 조회
    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByPost(@PathVariable("post_num") Long postNum) {
        List<CommentDto> comments = commentService.getCommentsByPost(postNum);
        return ResponseEntity.ok(comments);
    }

    // 댓글 작성
    @PostMapping
    public ResponseEntity<CommentDto> createComment(@PathVariable("post_num") Long postNum,
                                                    @RequestBody CommentDto commentDto,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        commentDto.setPostNum(postNum);

        if (userDetails.getRole().equals("ROLE_USER")) {
            commentDto.setUserNum(userDetails.getId());
        } else if (userDetails.getRole().equals("ROLE_PARTNER")) {
            commentDto.setPartnerNum(userDetails.getId());
        } else if (userDetails.getRole().equals("ROLE_ADMIN")) {
            commentDto.setAdminNum(userDetails.getId());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(commentDto));
    }

    // 댓글 수정
    @PatchMapping("/{comment_num}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable("comment_num") Long commentNum,
                                                    @RequestBody CommentDto commentDto,
                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<CommentDto> existingComment = commentService.getCommentById(commentNum);
        if (existingComment.isPresent() &&
                (existingComment.get().getUserNum() != null && existingComment.get().getUserNum().equals(userDetails.getId()) ||
                existingComment.get().getPartnerNum() != null && existingComment.get().getPartnerNum().equals(userDetails.getId()) ||
                existingComment.get().getAdminNum() != null && existingComment.get().getAdminNum().equals(userDetails.getId()))) {
            return ResponseEntity.ok(commentService.updateComment(commentNum, commentDto.getContent()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 댓글 삭제 - 관리자는 모든 댓글 삭제 가능
    @DeleteMapping("/{comment_num}")
    public ResponseEntity<?> deleteComment(@PathVariable("comment_num") Long commentNum,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<CommentDto> existingComment = commentService.getCommentById(commentNum);
        if (existingComment.isPresent()) {
            CommentDto existing = existingComment.get();
            boolean isOwner = false;

            // 작성자 본인 확인
            if (userDetails.getRole().equals("ROLE_USER") && existing.getUserNum() != null && existing.getUserNum().equals(userDetails.getId())) {
                isOwner = true;
            } else if (userDetails.getRole().equals("ROLE_PARTNER") && existing.getPartnerNum() != null && existing.getPartnerNum().equals(userDetails.getId())) {
                isOwner = true;
            } else if (userDetails.getRole().equals("ROLE_ADMIN") && existing.getAdminNum() != null && existing.getAdminNum().equals(userDetails.getId())) {
                isOwner = true;
            }

            // 본인 확인 또는 관리자 여부 확인
            if (isOwner || userDetails.getRole().equals("ROLE_ADMIN")) {
                commentService.deleteComment(commentNum);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
