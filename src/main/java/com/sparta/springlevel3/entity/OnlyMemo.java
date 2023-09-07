package com.sparta.springlevel3.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 클라이언트에게 반환할 일부 메모 정보를 담은 DTO
 */
@Getter
@Setter
public class OnlyMemo {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String title;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String contents;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime createdAt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime modifiedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    /**
     * 메모 정보를 저장하기 위한 생성자
     * @param memo  메모 정보
     */
    public OnlyMemo(Memo memo) {
        this.username = memo.getUsername();
        this.title = memo.getTitle();
        this.contents = memo.getContents();
        this.createdAt = memo.getCreatedAt();
        this.modifiedAt = memo.getModifiedAt();
    }

    /**
     * 메모의 일부 내용 ( StatusCode, Message )만 클라이언트에게 반환하기 위한 생성자
     * @param statusCode 상태 코드
     * @param message 메시지
     */

    public OnlyMemo(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
