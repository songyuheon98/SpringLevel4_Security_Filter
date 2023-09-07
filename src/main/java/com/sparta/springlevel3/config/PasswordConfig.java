package com.sparta.springlevel3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 암호화를 위한 설정
 */
@Configuration // 원하는 메서드 @Bean처리, 그 메서드를 담는 클래스 @Configuration 처리하면 수동 등록
public class PasswordConfig { // Bean에는 첫글자 소문자로 저장

    /**
     * 비밀번호 암호화에 필요한 PasswordEncoder Bean 등록
     * @return PasswordEncoder 구현체
     */
    @Bean
    public PasswordEncoder passwordEncoder() { // 구현체 등록
        return new BCryptPasswordEncoder(); // 비밀번호 암호화 해주는 해시 함수
    }
}
