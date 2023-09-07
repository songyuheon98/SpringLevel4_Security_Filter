package com.sparta.springlevel3.filter;

import com.sparta.springlevel3.entity.User;
import com.sparta.springlevel3.jwt.JwtUtil;
import com.sparta.springlevel3.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.io.PrintWriter;


/**
 * 클라이언트의 요청했을 때 특정 URL이나 Get 방식을 제외하고 모두 토큰의 유효성 및 인증 절차를 거치도록 하는 필터
 */

@Slf4j(topic = "AuthFilter")
@Component
@Order(2)
public class AuthFilter implements Filter {

    // 사용자 정보를 관리하는 저장소와 JWT 토큰 관련 기능을 제공하는 유틸리티 클래스를 멤버 변수로 선언
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 생성자를 통해 UserRepository와 JwtUtil 객체를 주입받음
    public AuthFilter(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 클라이언트가 요청 할때마다 거치는 필터
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException, IOException {
        // 현재의 요청 객체를 HttpServletRequest로 형변환하여 HTTP 관련 메서드를 사용
        /**
         * 중간에 예외 처리를 위한 HttpServletResponse 객체를 사용하기 위해 형변환
         * setContentype() 메서드를 사용하여 응답의 형식을 지정 한글 깨짐 방지
         */
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setContentType("text/html; charset=UTF-8");

        /**
         * 중강에 예외 처리를 위한 writer
         */
        PrintWriter writer;

        /**
         * 현재의 요청 객체를 HttpServletRequest로 형변환하여 HTTP 관련 메서드를 사용
         */
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;


        // 현재 요청의 URI를 가져옴
        String url = httpServletRequest.getRequestURI();
        String urlMethod = httpServletRequest.getMethod();


        HttpServletResponse httpResponse = (HttpServletResponse) response;


        // 로그인 API 요청에 대해 인증 절차를 건너뜀
        /**
         * 요청 URL이 /api/user로 시작하는 경우와 Get 방식인 경우 인증 절차를 건너뜀
         */
        if (StringUtils.hasText(url) && (url.startsWith("/api/user")||"GET".equals(urlMethod))) { // 로그인 + 회원가입 필터 우회
            chain.doFilter(request, response); // 인증없이 다음 Filter 또는 대상 Servlet/JSP로 요청 전달
        } else {
            /**
             * 인증이 필요한 API 요청에 대해서는 인증 절차를 진행
             */
            log.info("auth");

            // 그 외의 API 요청에 대해서는 인증 절차를 진행
            // JWT 토큰을 요청 헤더에서 가져옴
            String tokenValue = jwtUtil.getTokenFromRequest(httpServletRequest);
            /**
             * 토큰이 존재하는 경우
             * 토큰의 접두사를 제거하고 순수 토큰만 추출
             */
            if (StringUtils.hasText(tokenValue)) {
                // Bearer 등의 접두사를 제거하고 순수 토큰만 추출
                String token = jwtUtil.substringToken(tokenValue);

                /**
                 * 토큰의 유효성 검증
                 */
                if (!jwtUtil.validateToken(token)) {
                    httpServletResponse.setStatus(400);
                    writer= httpServletResponse.getWriter();

                    writer.println("{\n   \"status\":\"400\", ");
                    writer.println("   \"message\":\"토큰이 유효하지 않습니다 >.< !!! \"\n}");
                    writer.flush();
                    return;
                }

                /**
                 * 토큰에서 사용자 정보를 추출
                 * 토큰에서 추출한 사용자 정보를 바탕으로 데이터베이스에서 사용자 정보를 조회
                 */
                Claims info = jwtUtil.getUserInfoFromToken(token);
                User user = userRepository.findByUsername(info.getSubject()).orElseThrow(() ->
                        /**
                         * 사용자를 찾지 못할 경우 예외 발생
                         */
                      new NullPointerException("Not Found User") // 사용자를 찾지 못할 경우 예외 발생
                );

                /**
                 * 사용자 정보를 요청 객체에 저장
                 */
                request.setAttribute("user", user);
                /**
                 * 다음 필터 또는 대상 Servlet/JSP로 요청 전달
                 */
                chain.doFilter(request, response);
            } else {
                /**
                 * 토큰이 존재하지 않는 경우
                 * 400 에러를 반환
                 * JSON 형식으로 에러 메시지를 반환
                 * writer를 사용하여 JSON 형식으로 응답
                 * flush() 메서드를 사용하여 버퍼에 남아있는 데이터를 모두 출력
                 * return을 사용하여 다음 필터 또는 대상 Servlet/JSP로 요청을 전달하지 않음
                 */
                httpServletResponse.setStatus(400);
                writer= httpServletResponse.getWriter();

                writer.println("{\n \"status\":\"400\", ");
                writer.println(" \"message\":\"토큰이 유효하지 않습니다 >.< !!! \"\n}");
                writer.flush();
                return;
            }
        }
    }
}
