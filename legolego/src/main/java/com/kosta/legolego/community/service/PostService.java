package com.kosta.legolego.community.service;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.community.dto.PostDto;
import com.kosta.legolego.community.entity.Post;
import com.kosta.legolego.community.repository.PostRepository;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.security.CustomUserDetails;
import com.kosta.legolego.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    // 전체 게시글 리스트 조회
    public List<PostDto> getAllPosts() {
        return postRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 상세 조회
    public Optional<PostDto> getPostById(Long postNum) {
        return postRepository.findById(postNum).map(this::convertToDto);
    }

    // 내가 쓴 글 목록 조회
    public List<PostDto> getPostsByUser(CustomUserDetails userDetails) {
        List<Post> posts;
        if (userDetails.getRole().equals("ROLE_USER")) {
            User user = new User();
            user.setUserNum(userDetails.getId());
            posts = postRepository.findByUser(user);
        } else if (userDetails.getRole().equals("ROLE_PARTNER")) {
            Partner partner = new Partner();
            partner.setPartnerNum(userDetails.getId());
            posts = postRepository.findByPartner(partner);
        } else if (userDetails.getRole().equals("ROLE_ADMIN")) {
            Admin admin = new Admin();
            admin.setAdminNum(userDetails.getId());
            posts = postRepository.findByAdmin(admin);
        } else {
            posts = List.of();
        }
        return posts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 내가 댓글 단 글 목록 조회
    public List<PostDto> getPostsCommentedByUser(CustomUserDetails userDetails) {
        List<Post> posts;
        if (userDetails.getRole().equals("ROLE_USER")) {
            User user = new User();
            user.setUserNum(userDetails.getId());
            posts = postRepository.findByCommentsUser(user);
        } else if (userDetails.getRole().equals("ROLE_PARTNER")) {
            Partner partner = new Partner();
            partner.setPartnerNum(userDetails.getId());
            posts = postRepository.findByCommentsPartner(partner);
        } else if (userDetails.getRole().equals("ROLE_ADMIN")) {
            Admin admin = new Admin();
            admin.setAdminNum(userDetails.getId());
            posts = postRepository.findByCommentsAdmin(admin);
        } else {
            posts = List.of();
        }
        return posts.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // 게시글 작성
    public PostDto createPost(PostDto postDto) {
        Post post = convertToEntity(postDto);
        post.setRegDate(LocalDate.now());
        return convertToDto(postRepository.save(post));
    }

    // 게시글 수정
    public PostDto updatePost(PostDto postDto) {
        Optional<Post> optionalPost = postRepository.findById(postDto.getPostNum());
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setTitle(postDto.getTitle());
            post.setContent(postDto.getContent());
            post.setPostCategory(postDto.getCategory());
            post.setModDate(LocalDate.now());
            return convertToDto(postRepository.save(post));
        } else {
            throw new RuntimeException("게시글을 찾을 수 없습니다.");
        }
    }

    // 게시글 삭제
    public void deletePost(Long postNum) {
        postRepository.deleteById(postNum);
    }

    private PostDto convertToDto(Post post) {
        return new PostDto(post.getPostNum(), post.getTitle(), post.getContent(),
                            post.getUser() != null ? post.getUser().getUserNum() : null,
                            post.getPartner() != null ? post.getPartner().getPartnerNum() : null,
                            post.getAdmin() != null ? post.getAdmin().getAdminNum() : null,
                            post.getRegDate(), post.getModDate(), post.getPostCategory());
    }

    private Post convertToEntity(PostDto postDto) {
        Post post = new Post();
        post.setPostNum(postDto.getPostNum());
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        if (postDto.getUserNum() != null) {
            User user = new User();
            user.setUserNum(postDto.getUserNum());
            post.setUser(user);
        } else if (postDto.getPartnerNum() != null) {
            Partner partner = new Partner();
            partner.setPartnerNum(postDto.getPartnerNum());
            post.setPartner(partner);
        } else if (postDto.getAdminNum() != null) {
            Admin admin = new Admin();
            admin.setAdminNum(postDto.getAdminNum());
            post.setAdmin(admin);
        }
        post.setRegDate(postDto.getRegDate());
        post.setModDate(postDto.getModDate());
        post.setPostCategory(postDto.getCategory());
        return post;
    }
}
