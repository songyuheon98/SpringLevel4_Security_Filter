package com.sparta.springlevel3.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 클라이언트가 작성한 로그인 정보를 담은 DTO
 */
@Setter
@Getter
public class LoginDto {
    private String username;
    private String password;
}
