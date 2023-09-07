package com.sparta.springlevel3.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 클라이언트가 작성한 회원가입 정보를 담은 DTO
 */
@Setter
@Getter
public class SignUpDto {
    private String username;
    private String password;
    private Boolean isAdmin = false;
    private String adminToken = "";
}
