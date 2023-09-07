package com.sparta.springlevel3.config;

import com.sparta.springlevel3.jwt.JwtAuthenticationFilter;
import com.sparta.springlevel3.jwt.JwtAuthorizationFilter;
import com.sparta.springlevel3.jwt.JwtUtil;
import com.sparta.springlevel3.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정
 * - CSRF 설정
 * - 로그인 사용
 * - resources 폴더에 있는 파일들에 대한 접근 허용
 * - 그 외 모든 요청에 대해 인증 처리
 * @Configuration: 스프링 설정 파일
 * - @Bean: 스프링이 관리하는 객체
 * - SecurityFilterChain: Spring Security 설정
 * - HttpSecurity: HTTP 요청에 대한 웹 기반 보안을 구성하는 데 사용
 * - csrf: Cross Site Request Forgery
 * - PathRequest: Spring Security에서 제공하는 정적 자원에 대한 요청을 허용하는 클래스
 * @EnableWebSecurity: Spring Security 지원을 가능하게 함
 */
@Configuration
@EnableWebSecurity // Spring Security 지원을 가능하게 함
public class WebSecurityConfig {
    /**
     * Spring Security 설정
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception
     */
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;

    /**
     * 생성자 주입
     * @param jwtUtil JWT 토큰을 생성하고 검증하는 컴포넌트
     * @param userDetailsService 사용자 정보를 가져오는 서비스
     * @param authenticationConfiguration 인증을 위한 설정
     */
    public WebSecurityConfig(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, AuthenticationConfiguration authenticationConfiguration) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    /**
     * AuthenticationManager를 Bean으로 등록
     * @param configuration 인증을 위한 설정
     * @return AuthenticationManager 객체 반환
     * @throws Exception 예외 처리
     */
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * JWT 토큰을 검증하고 인증하는 필터
     * @return JwtAuthorizationFilter 객체 반환
     */
    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userDetailsService);
    }

    /**
     * 로그인 및 JWT 토큰을 생성하기 위한 필터
     * @return JwtAuthenticationFilter 객체 반환
     * @throws Exception
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);
        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
        return filter;
    }



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /**
         * CSRF 설정
         * - CSRF 토큰을 사용하지 않음
         * - CSRF 토큰을 사용하면, CSRF 토큰을 포함하지 않은 요청은 모두 차단됨
         */
        http.csrf((csrf) -> csrf.disable());

        /**
         * Session 방식이 아닌 JWT 토큰 방식을 사용하도록 설정
         */
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        /**
         * /api/user/ 아래 모든 요청은 인증 없이 접근 가능하도록 설정
         * GET 메소드 요청은 인증 없이 접근 가능하도록 설정
         * 그 외 모든 요청은 인증을 필요로 함
         */
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers("/api/user/**").permitAll() // 메모 관련 API 접근 허용 설정
                        .requestMatchers(HttpMethod.GET).permitAll() // GET 메소드 요청 허용 설정
                        .anyRequest().authenticated() // 그 외 모든 요청 인증처리
        );

        /**
         * JWT 토큰을 검증하고 인증하는 필터 ( JWTAuthorizationFilter ) 를 JwtAuthenticationFilter 앞에 설정
         * 로그인 및 JWT 토큰을 생성하기 위한 필터 ( JwtAuthenticationFilter ) 를 UsernamePasswordAuthenticationFilter 앞에 설정
         */
        http.addFilterBefore(jwtAuthorizationFilter(), JwtAuthenticationFilter.class);
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        /**
         * http.build() 메소드를 통해 설정한 내용을 적용하여 SecurityFilterChain 객체를 생성하여 반환
         */
        return http.build();

    }

}