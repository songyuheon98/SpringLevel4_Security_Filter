package com.sparta.springlevel3.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 클라이언트의 요청 정보를 로그로 남기는 필터
 */
@Slf4j(topic = "LoggingFilter")
@Component
@Order(1)
public class LoggingFilter implements Filter {
    /**
     * 클라이언트의 요청 정보를 로그로 남김
     * @param request  클라이언트의 요청 정보
     * @param response 클라이언트에게 응답할 정보
     * @param chain   다음 필터로 요청과 응답을 전달
     * @throws ServletException 서블릿 예외
     * @throws IOException      IO 예외
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        /**
         * HttpServletRequest의 getRequestURI() 메서드를 사용하여 클라이언트가 요청한 URI를 가져옴
         */
        String url = httpServletRequest.getRequestURI();

        /**
         * 요청 URL 정보를 로그로 남김
         */
        log.info(url);

        /**
         * 다음 필터로 요청과 응답을 전달
         */
        chain.doFilter(request, response);

        /**
         * 비즈니스 로직이 완료된 후에도 로그를 남김
         */
        log.info("비즈니스 로직 완료");
    }

}