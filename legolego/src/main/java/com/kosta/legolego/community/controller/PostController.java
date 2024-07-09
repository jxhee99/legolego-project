package com.kosta.legolego.community.controller;

import com.kosta.legolego.community.dto.PostDto;
import com.kosta.legolego.community.service.PostService;
import com.kosta.legolego.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // 전체 게시글 리스트 조회
    @GetMapping("/all")
    public List<PostDto> getAllPosts() {
        return postService.getAllPosts();
    }

    // 상세 조회
    @GetMapping("/{post_num}")
    public ResponseEntity<?> getPostByPostNum(@PathVariable("post_num") Long postNum,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<PostDto> postDto = postService.getPostById(postNum);
        if (postDto.isPresent()) {
            return ResponseEntity.ok(postDto);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    // 내가 쓴 글 목록 조회
    @GetMapping("/my-posts")
    public ResponseEntity<List<PostDto>> getMyPosts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PostDto> myPosts = postService.getPostsByUser(userDetails);
        return ResponseEntity.ok(myPosts);
    }

    // 내가 댓글 단 글 목록 조회
    @GetMapping("/my-comments")
    public ResponseEntity<List<PostDto>> getMyCommentedPosts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PostDto> commentedPosts = postService.getPostsCommentedByUser(userDetails);
        return ResponseEntity.ok(commentedPosts);
    }

    // 게시글 작성
    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (userDetails.getRole().equals("ROLE_USER")) {
            postDto.setUserNum(userDetails.getId());
        } else if (userDetails.getRole().equals("ROLE_PARTNER")) {
            postDto.setPartnerNum(userDetails.getId());
        } else if (userDetails.getRole().equals("ROLE_ADMIN")) {
            postDto.setAdminNum(userDetails.getId());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(postDto));
    }

    // 게시글 수정
    @PatchMapping("/{post_num}")
    public ResponseEntity<PostDto> updatePost(@PathVariable("post_num") Long postNum,
                                              @RequestBody PostDto postDto,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<PostDto> existingPost = postService.getPostById(postNum);
        if (existingPost.isPresent()) {
            PostDto existing = existingPost.get();
            boolean isOwner = false;

            if (userDetails.getRole().equals("ROLE_USER") && existing.getUserNum() != null && existing.getUserNum().equals(userDetails.getId())) {
                isOwner = true;
            } else if (userDetails.getRole().equals("ROLE_PARTNER") && existing.getPartnerNum() != null && existing.getPartnerNum().equals(userDetails.getId())) {
                isOwner = true;
            } else if (userDetails.getRole().equals("ROLE_ADMIN") && existing.getAdminNum() != null && existing.getAdminNum().equals(userDetails.getId())) {
                isOwner = true;
            }

            if (isOwner) {
                postDto.setPostNum(postNum);
                return ResponseEntity.ok(postService.updatePost(postDto));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // 게시글 삭제 - 관리자는 모든 게시글 삭제 가능
    @DeleteMapping("/{post_num}")
    public ResponseEntity<?> deletePost(@PathVariable("post_num") Long postNum,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<PostDto> existingPost = postService.getPostById(postNum);
        if (existingPost.isPresent()) {
            PostDto existing = existingPost.get();
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
                postService.deletePost(postNum);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
