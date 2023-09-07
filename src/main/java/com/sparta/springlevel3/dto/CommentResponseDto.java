package com.sparta.springlevel3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.springlevel3.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 클라이언트에게 반환할 댓글 정보를 담은 DTO
 */
@Getter
public class CommentResponseDto { // 응답하는 Dto
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long postid;
    private String contents;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    /**
     * 댓글 정보를 저장하기 위한 생성자
     * @param comment
     */
    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.postid = comment.getPostid();
        this.contents = comment.getContents();
        this.username = comment.getUsername();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }

    public CommentResponseDto() {}

    /**
     * 댓글의 일부 내용만 클라이언트에게 반환하기 위한 생성자
     * @param id 댓글의 ID
     * @param contents 댓글 내용
     * @param username 댓글 작성자
     * @param createdAt 댓글 작성 시간
     * @param modifiedAt 댓글 수정 시간
     */
    public CommentResponseDto(Long id, String contents, String username, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.contents = contents;
        this.username = username;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    /**
     * 댓글 정보를 클라이언트에게 반환하기 위해 CommentResponseDto 형태로 변환
     * @param comment 댓글 정보
     * @return  댓글 정보를 클라이언트에게 반환하기 위해 CommentResponseDto 형태로 변환
     */
    public CommentResponseDto fromComment(Comment comment) {
        return new CommentResponseDto(comment.getId(), comment.getContents(), comment.getUsername(), comment.getCreatedAt(), comment.getModifiedAt());
    }
}
