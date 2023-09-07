package com.sparta.springlevel3.dto;

import com.sparta.springlevel3.entity.Comment;
import com.sparta.springlevel3.entity.Memo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 클라이언트에게 반환할 댓글 정보를 담은 DTO
 */
@Getter
@Setter
public class MemoCommentDto {

    private Long id;
    private String title;
    private String contents;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    List<CommentResponseDto> comments;

    public MemoCommentDto(Memo memo, List<CommentResponseDto> comments) {
        this.id = memo.getId();
        this.title = memo.getTitle();
        this.contents = memo.getContents();
        this.username = memo.getUsername();
        this.createdAt = memo.getCreatedAt();
        this.modifiedAt = memo.getModifiedAt();
        this.comments = comments;
    }
}
