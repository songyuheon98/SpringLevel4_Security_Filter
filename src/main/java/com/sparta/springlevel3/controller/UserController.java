package com.sparta.springlevel3.controller;

import com.sparta.springlevel3.dto.LoginDto;
import com.sparta.springlevel3.dto.SignUpDto;
import com.sparta.springlevel3.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 관련 HTTP 요청 처리를 담당하는 컨트롤러
 */
@RestController // 그냥 컨트롤러라 return값 뒤에 html 붙음
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * 클라이언트에게 받은 회원 가입 정보를 바탕으로 회원 가입을 진행
     * @param requestDto 클라이언트가 보낸 회원 가입 정보를 담은 DTO
     * @return 회원 가입이 완료된 경우, 회원 가입 완료 메시지를 반환
     */
    @PostMapping("/user/signup")
    public ResponseEntity<String> signup(@RequestBody SignUpDto requestDto) {

        return ResponseEntity.ok(userService.signup(requestDto));

    }
}
