package com.sparta.springlevel3.jwt;

import com.sparta.springlevel3.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰을 검증하고 인증하는 필터
 */
@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * JWT 토큰을 검증하고 인증하는 메소드
     *
     * @param req         HTTP 요청
     * @param res         HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 예외 처리
     * @throws IOException      예외 처리
     */
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String tokenValue = jwtUtil.getTokenFromRequest(req);

        /**
         * JWT 토큰이 존재하는지 확인 후, 토큰이 존재하면 Substring을 통해 토큰을 잘라내고,
         * TokenValue에 저장한다.
         * 토큰이 없으면 다음 필터로 넘어간다.
         */
        if (StringUtils.hasText(tokenValue)) {
            // JWT 토큰 substring
            tokenValue = jwtUtil.substringToken(tokenValue);
            log.info(tokenValue);

            /**
             * 토큰이 유효한지 확인한다.
             */
            if (!jwtUtil.validateToken(tokenValue)) {
                log.error("Token Error");
                return;
            }

            /**
             * 토큰에서 사용자 정보를 추출하여 info 인증 객체를 생성한다.
             */
            Claims info = jwtUtil.getUserInfoFromToken(tokenValue);

            /**
             * info의 사용자 정보를 기반으로 SecurityContext에 인증 객체를 저장한다.
             */
            try {
                setAuthentication(info.getSubject());
            } catch (Exception e) {
                log.error(e.getMessage());
                return;
            }
        }
        filterChain.doFilter(req, res);
    }

    /**
     * SecurityContext에 인증 객체를 저장하는 메소드
     *
     * @param username 사용자 이름
     */
    public void setAuthentication(String username) {
        /**
         * 사용자의 인증정보(Authentication 객체)를 담기위한 객체를 생성한다.
         * SecurityContextHolder의 createEmptyContext() 메소드를 통해 SecurityContext를 생성가능하다.
         * SecurityContexHolder은 현재 요청을 처리하는 스레드에 대한 SecurityContext를 제공한다.
         */
        SecurityContext context = SecurityContextHolder.createEmptyContext();


        /**
         * 주어진 사용자 이름(username)을 기반으로 인증 객체를 생성한다.
         */
        Authentication authentication = createAuthentication(username);

        /**
         * 현재 쓰레드의 SecurityContext에 인증 객체를 저장한다.
         */
        context.setAuthentication(authentication);

        /**
         * SecurityContext를 SecurityContextHolder에 저장한다.
         * SecurityContextHolder을 사용해서 사용자의 인증정보를 어디서든지 접근 가능하다.
         */
        SecurityContextHolder.setContext(context);
    }

    /**
     * 사용자 이름을 기반으로 인증 객체를 생성하는 메소드
     *
     * @param username 사용자 이름
     * @return 인증 객체
     */
    private Authentication createAuthentication(String username) {
        /**
         * userDetailsService의 loadUserByUsername() 메소드를 사용해서 사용자 정보를 가져온다.
         * UserDetailsImpl은 UserDetails 인터페이스를 구현한 클래스이다.
         */
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        /**
         * UsernamePasswordAuthenticationToken은 Authentication 인터페이스를 구현한 클래스이다.
         * 주로 사용자의 인증을 위해 사용된다.
         * new UsernamePasswordAuthenticationToken(인증객체, 패스워드, 권한) 형태로 생성한다.
         * 이 함수의 목적은 주어진 사용자 이름에 맞는 사용자의 인증 토큰을 생성하는 것이다.
         */
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    }
}