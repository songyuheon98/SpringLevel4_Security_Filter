package com.sparta.springlevel3.dto;


import lombok.Getter;


/**
 * 클라이언트가 요청한 메모 정보를 담은 DTO
 */
@Getter
public class MemoRequestDto { // 정보 주는 Dto
    private String username;
    private String title;
    private String contents;
}
