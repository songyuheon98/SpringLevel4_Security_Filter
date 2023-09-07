package com.sparta.springlevel3.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.springlevel3.dto.LoginDto;
import com.sparta.springlevel3.entity.UserRoleEnum;
import com.sparta.springlevel3.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 로그인 및 JWT 토큰을 생성하기 위한 필터
 */
@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtUtil jwtUtil;

    private PrintWriter writer;

    /**
     * JwtAuthenticationFilter 생성자
     * setFilterProcessesUrl: 로그인 요청 URL 설정
     * @param jwtUtil
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/api/user/login");
    }


    /**
     * 로그인 시도 시에 실행되는 메소드
     * @param request http 요청 객체
     * @param response http 응답 객체
     * @return Authentication 객체
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("로그인 시도");

        try {
            /**
             * ObjectMapper를 통해 request.getInputStream()으로 받은 JSON 데이터를 Java객체 ( LoginDto )로 변환한다.
             * ObjectMapper = Jackson 라이브러리에서 제공하는 클래스
             * readValue() 메소드를 통해 JSON 데이터를 Java 객체 ( LoginDto )로 변환한다.
             */
            LoginDto requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginDto.class);

            /**
             * UsernamePasswordAuthenticationToken 객체를 생성한다.
             * 이 객체는 인증을 위해 필요한 객체로서, 생성자의 파라미터로 ID와 Password를 받는다.
             * 이후 AuthenticationManager를 통해 사용자 인증을 시도한다.
             */
            return getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.getUsername(),
                            requestDto.getPassword(),
                            null
                    )
            );
            /**
             * 오류가 발생하면 RuntimeException을 발생시킨다.
             */
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 로그인 성공 시에 실행되는 메소드
     * @param request http 요청 객체
     * @param response http 응답 객체
     * @param chain 필터 체인
     * @param authResult 인증 객체
     * @throws IOException 예외 처리
     * @throws ServletException 예외 처리
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공 및 JWT 생성");
        /**
         * 로그인 성공한 이후 respnse 객체에 한글 깨짐 현상을 방지하기 위해 setContentType을 설정한다.
         */
        response.setContentType("text/html; charset=UTF-8");

        /**
         * UserDetailsImpl 객체에서 사용자 정보를 추출한다.
         * UserDetailsImpl 객체에서 사용자 권한 정보를 추출한다.
         */
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        /**
         * JWT 토큰을 생성한다.
         * 사용자 이름과 유형을 파라미터로 전달한다.
         */
        String token = jwtUtil.createToken(username, role);

        /**
         * JWT 토큰을 Cookie에 저장한다.
         */
        jwtUtil.addJwtToCookie(token, response);

        /**
         * 로그인 성공 메시지를 응답한다.
         * 응답 코드는 200으로 설정한다.
         */
        response.setStatus(200);
        writer= response.getWriter();

        writer.println("{\n   \"status\":\"200\", ");
        writer.println("   \"message\":\"로그인 성공 >.< !!! \"\n}");
        writer.flush();
        return;
    }

    /**
     * 로그인 실패 시에 실행되는 메소드
     * @param request http 요청 객체
     * @param response http 응답 객체
     * @param failed 인증 실패 객체
     * @throws IOException 예외 처리
     * @throws ServletException 예외 처리
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        /**
         * 로그인 실패 메시지를 응답한다.
         * 응답 코드는 401으로 설정한다.
         */
        response.setContentType("text/html; charset=UTF-8");
        log.info("로그인 실패");
        response.setStatus(401);
        writer= response.getWriter();
        writer.println("{\n   \"status\":\"401\", ");
        writer.println("   \"message\":\"로그인 성공 >.< !!! \"\n}");
        writer.flush();
        return;
    }
}