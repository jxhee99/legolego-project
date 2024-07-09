package com.kosta.legolego.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long commentNum;
    private Long postNum;
    private Long userNum;
    private Long partnerNum;
    private Long adminNum;
    private String content;
    private LocalDate regDate;
    private LocalDate modDate;
    private Long parentCommentNum;
    private Boolean deleted;
    private List<CommentDto> replies;
}
