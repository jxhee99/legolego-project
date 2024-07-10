package com.kosta.legolego.community.dto;

import com.kosta.legolego.community.entity.Post.PostCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostDto {

    private Long postNum;
    private String title;
    private String content;
    private Long userNum;
    private Long partnerNum;
    private Long adminNum;
    private LocalDate regDate;
    private LocalDate modDate;
    private PostCategory category;
    private int viewCount;
    private int commentCount;
}
