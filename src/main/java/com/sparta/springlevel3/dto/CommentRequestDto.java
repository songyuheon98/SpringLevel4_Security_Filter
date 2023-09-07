package com.sparta.springlevel3.dto;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
/**
 * 클라이언트가 작성할 댓글 정보를 담은 DTO
 */
public class CommentRequestDto {
    private Long postid;
    private String contents;
}