package com.kosta.legolego.community.service;

import com.kosta.legolego.admin.entity.Admin;
import com.kosta.legolego.community.dto.PostDto;
import com.kosta.legolego.community.entity.Post;
import com.kosta.legolego.community.entity.Post.PostCategory;
import com.kosta.legolego.community.repository.PostRepository;
import com.kosta.legolego.partner.entity.Partner;
import com.kosta.legolego.security.CustomUserDetails;
import com.kosta.legolego.user.entity.User;
import jakarta.transaction.Transactional;
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

    // 모든 게시글 최신순 정렬
    public List<PostDto> getAllPostsByLatest() {
        return postRepository.findAllByOrderByRegDateDesc().stream()
                .map(this::convertToDto).collect(Collectors.toList());
    }

    // 모든 게시글 오래된 순으로 정렬
    public List<PostDto> getAllPostsByOldest() {
        return postRepository.findAllByOrderByRegDateAsc().stream()
                .map(this::convertToDto).collect(Collectors.toList());
    }

    // 상세 조회 및 조회수 증가
    @Transactional
    public Optional<PostDto> getPostById(Long postNum) {
        return postRepository.findById(postNum).map(post -> {
            post.incrementViewCount();
            postRepository.save(post);
            return convertToDto(post);
        });
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

    // 검색
    public List<PostDto> searchPostsByKeyword(String keyword) {
        return postRepository.searchPostsByKeyword(keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 카테고리별 키워드 검색
    public List<PostDto> searchPostsByCategoryAndKeyword(PostCategory category, String keyword) {
        return postRepository.findByPostCategoryAndKeyword(category, keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 카테고리별 게시글 최신순 정렬 (기본)
    public List<PostDto> getPostsByCategoryLatest(PostCategory category) {
        return postRepository.findByPostCategoryOrderByRegDateDesc(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 카테고리별 게시글 오래된 순으로 정렬
    public List<PostDto> getPostsByCategoryOldest(PostCategory category) {
        return postRepository.findByPostCategoryOrderByRegDateAsc(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private PostDto convertToDto(Post post) {
        return new PostDto(post.getPostNum(), post.getTitle(), post.getContent(),
                            post.getUser() != null ? post.getUser().getUserNum() : null,
                            post.getPartner() != null ? post.getPartner().getPartnerNum() : null,
                            post.getAdmin() != null ? post.getAdmin().getAdminNum() : null,
                            post.getUser() != null ? post.getUser().getUserNickname() : null,
                            post.getPartner() != null ? post.getPartner().getCompanyName() : null,
                            post.getAdmin() != null ? post.getAdmin().getAdminName() : null,
                            post.getRegDate(), post.getModDate(), post.getPostCategory(),
                            post.getViewCount(), post.getCommentCount());
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
        post.setViewCount(postDto.getViewCount());
        post.setCommentCount(postDto.getCommentCount());
        return post;
    }
}
