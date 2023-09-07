package com.sparta.springlevel3.service;

import com.sparta.springlevel3.dto.LoginDto;
import com.sparta.springlevel3.dto.SignUpDto;
import com.sparta.springlevel3.entity.User;
import com.sparta.springlevel3.entity.UserRoleEnum;
import com.sparta.springlevel3.jwt.JwtUtil;
import com.sparta.springlevel3.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
/**
 * 회원가입, 로그인, 권한 확인 등을 처리하는 서비스
 */
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    /**
     * 생성자: UserService 클래스가 생성될 때 스프링에서 자동으로 실행됨
     * @param userRepository
     * @param passwordEncoder
     * @param jwtUtil
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    // ADMIN_TOKEN 일반 사용자인지 관리자인지
    /**
     * 관리자 암호
     */
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";
    /**
     * 회원가입 시 사용되는 ID 정규 표현식
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9]{4,10}$");
    /**
     * 비밀번호 정규 표현식
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()]{8,15}$");

    /**
     * 회원가입 시 ID를 확인하는데 사용되는 정규 표현식을
     * @param username 사용자가 입력한 ID
     * @return 정규 표현식에 맞는 경우 true, 아닌 경우 false
     */
    public static boolean isValidUsername(String username) {
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * 회원가입 시 비밀번호를 확인하는데 사용되는 정규 표현식을
     * @param password 사용자가 입력한 비밀번호
     * @return 정규 표현식에 맞는 경우 true, 아닌 경우 false
     */

    public static boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * 회원가입을 처리하는 메서드
     * @param requestDto 사용자가 입력한 회원가입 정보
     * @return 회원가입이 완료된 경우 회원가입 완료 메시지 및 Status Code 200 반환
     */
    public String signup(SignUpDto requestDto) {
        /**
         * @param username 사용자가 입력한 ID
         *                 (영어 소문자, 숫자, 4~10자)
         * @param password 사용자가 입력한 비밀번호
         *                 (영어 대소문자, 숫자, 특수문자, 8~15자)
         * @param isAdmin  관리자 권한 부여 여부
         *                 (true: 관리자, false: 일반 사용자)
         * @param adminToken 관리자 암호
         *                   (관리자 권한 부여 시 필요한 암호)
         */
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        Boolean isAdmin = requestDto.getIsAdmin();
        String adminToken = requestDto.getAdminToken();

        /**
         * 회원가입 시 입력한 ID와 비밀번호가 정규 표현식에 맞지 않는 경우
         */
        if (!isValidPassword(password))
            throw new IllegalArgumentException("PW 형태가 부적절합니다.");
        else
            password = passwordEncoder.encode(password);

        if (!isValidUsername(username))
            throw new IllegalArgumentException("ID 형태가 부적절합니다.");


        /**
         * 회원가입 시 입력한 ID가 이미 존재하는지 확인
         */
        Optional<User> checkUsername = userRepository.findByUsername(username); // 쿼리 메서드 사용

        /**
         * 이미 존재하는 ID인 경우
         */
        if (checkUsername.isPresent()) {
            return "{\"message\": \"중복된 username 입니다\", \"statusCode\": 400}";
        }

        /**
         * role 설정
         */
        UserRoleEnum role = UserRoleEnum.USER; // 일단 일반 사용자 권한 넣어 놓음

        /**
         * isAdmin이 true이고 관리자 암호가 일치하는 경우 role에 관리자 권한 부여
         * 만약 isAdmin이 true이고 관리자 암호가 일치하지 않는 경우
         * IllegalArgumentException 발생 시키고 회원가입 불가
         */
        if (isAdmin == true) {
            if (!ADMIN_TOKEN.equals(adminToken)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }

        /**
         * 회원가입 정보 저장
         */
        User user = new User(username, password, role);
        /**
         * 회원가입 정보 userRepository를 활용해서 DB에 저장
         */
        userRepository.save(user);

        /**
         * 회원가입 완료 메시지 및 Status Code 200 반환
         */
        return "{\"message\": \"회원가입 성공\", \"statusCode\": 200}";
    }


    /**
     * 로그인을 처리하는 메서드
     * @param requestDto 사용자가 입력한 로그인 정보
     * @param res client에게 토큰을 전달하기 위한 response 객체
     * @return 로그인이 완료된 경우 로그인 완료 메시지 및 Status Code 200 반환
     */
    public ResponseEntity<String> login(LoginDto requestDto, HttpServletResponse res) {
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();

        /**
         * 로그인 시 입력한 ID가 DB에 존재하는지 확인
         */
        User user = userRepository.findByUsername(username).orElse(null);

        /**
         * 존재하지 않는 ID인 경우
         */
        if (user == null) {
            return ResponseEntity.badRequest().body("{\"message\": \"등록된 사용자가 없습니다\", \"statusCode\": 400}");
        }

        /**
         * 입력한 비밀번호와 DB에 저장된 비밀번호가 일치하는지 확인
         */
        if (!passwordEncoder.matches(password,user.getPassword()))
            return ResponseEntity.badRequest().body("{\"message\": \"PW가 일치하지 않습니다.\", \"statusCode\": 400}");

        /**
         * 토큰 생성 및 쿠키에 토큰 추가
         */
        String token = jwtUtil.createToken(user.getUsername(), user.getRole());
        jwtUtil.addJwtToCookie(token, res);

        /**
         * 로그인 완료 메시지 및 Status Code 200 반환
         */
        return ResponseEntity.ok(("{\"message\": \"로그인 성공\", \"statusCode\": 200}"));
    }

}
