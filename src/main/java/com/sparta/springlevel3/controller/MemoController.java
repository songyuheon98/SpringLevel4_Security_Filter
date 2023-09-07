package com.sparta.springlevel3.controller;

import com.sparta.springlevel3.dto.MemoCommentResponseAllDto;
import com.sparta.springlevel3.dto.MemoRequestDto;
import com.sparta.springlevel3.dto.MemoResponseDto;
import com.sparta.springlevel3.entity.OnlyMemo;
import com.sparta.springlevel3.entity.User;
import com.sparta.springlevel3.service.MemoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 메모 관련 HTTP 요청 처리를 담당하는 컨트롤러
 */
@RestController
@RequestMapping("/api")
public class MemoController {
    private final MemoService memoService;

    /**
     * 생성자: MemoController 클래스가 생성될 때 스프링에서 자동으로 실행됨
     * @param memoService MemoService를 주입받음
     */
    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    /**
     * 클라이언트에게 받은 정보를 바탕으로 메모를 생성하고, 생성된 메모 반환형식에 맞게 MemoResponseDto 를 만들어 반환
     * @param requestDto 클라이언트가 보낸 회원 가입 정보를 담은 DTO
     * @param req    필터에서 저장한 토큰의 유저 정보를 가져오기 위한 HttpServletRequest
     * @return 생성된 메모에 대한 정보를 클라이언트에게 알려주기 위해 MemoResponseDto 형태로 반환
     */
    @PostMapping("/memos")
    public MemoResponseDto createMemo(@RequestBody MemoRequestDto requestDto, HttpServletRequest req) {
        User user = (User) req.getAttribute("user");
        return memoService.createMemo(requestDto, user.getUsername());
    }

    /**
     * DB에 저장된 모든 메모와 댓글들을 Client에게 반환
     * Client의 모든 메모 조회 요청을 처리하고, 조회된 메모와 그에 담긴 댓글을 반환형식에 맞게 MemoCommentResponseAllDto 를 만들어 반환
     * @return
     */
    @GetMapping("/memos")
    public List<MemoCommentResponseAllDto> getMemos() {
        return memoService.getMemos();
    }

    /**
     * 클라이언트에게 받은 메모 ID를 바탕으로 메모를 조회하고, 조회된 메모와 댓글들을 반환형식에 맞게 MemoCommentResponseAllDto 를 만들어 반환
     * @param id 클라이언트가 요청한 메모의 ID
     * @return 조회된 메모와 그에 담긴 댓글을 반환형식에 맞게 MemoCommentResponseAllDto 를 만들어 반환
     */
    @GetMapping("/memos/{id}")
    public MemoCommentResponseAllDto getOneMemo(@PathVariable Long id) {
        return memoService.getOneMemo(id);
    }

    /**
     * 클라이언트에게 받은 메모 ID를 바탕으로 메모를 수정하고, 수정된 메모를 반환형식에 맞게 MemoResponseDto 를 만들어 반환
     *
     * @param id         클라이언트가 요청한 수정할 메모의 ID
     * @param requestDto 클라이언트가 보낸 메모 수정 정보를 담은 DTO
     * @param req        필터에서 저장한 토큰의 유저 정보를 가져오기 위해 사용
     * @return 수정된 메모를 반환형식에 맞게 MemoResponseDto 를 만들어 반환
     */
    @PutMapping("/memos/{id}")
    public OnlyMemo updateMemo(@PathVariable Long id, @RequestBody MemoRequestDto requestDto, HttpServletRequest req) {
        User user = (User) req.getAttribute("user");

        return memoService.updateMemo(id, requestDto, user.getUsername(), user.getRole());
    }

    /**
     * 클라이언트에게 받은 메모 ID를 바탕으로 메모를 삭제하고, 삭제된 메모의 ID를 반환형식에 맞게 String 으로 만들어 반환
     * @param id 클라이언트가 요청한 삭제할 메모의 ID
     * @param req 필터에서 저장한 토큰의 유저 정보를 가져오기 위해 사용
     * @return 삭제 여부를 알려주는 메시지
     */
    @DeleteMapping("/memos/{id}")
    public ResponseEntity<String> deleteMemo(@PathVariable Long id, HttpServletRequest req) {
        User user = (User) req.getAttribute("user");

        return memoService.deleteMemo(id, user.getUsername(), user.getRole());
    }
}