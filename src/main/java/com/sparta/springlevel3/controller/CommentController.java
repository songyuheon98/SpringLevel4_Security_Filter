package com.sparta.springlevel3.controller;

import com.sparta.springlevel3.dto.CommentRequestDto;
import com.sparta.springlevel3.dto.CommentResponseDto;
import com.sparta.springlevel3.entity.User;
import com.sparta.springlevel3.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * 댓글 관련 HTTP 요청 처리를 담당하는 컨트롤러
 */
@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 클라이언트에게 받은 정보를 바탕으로 댓글을 생성하고, 생성된 댓글 반환형식에 맞게 CommentResponseDto 를 만들어 반환
     * @param requestDto 클라이언트가 작성할 댓글 정보를 담은 DTO
     * @param req 필터에서 저장한 토큰의 유저 정보를 가져오기 위한 HttpServletRequest
     * @return 생성된 댓글에 대한 정보를 클라이언트에게 알려주기 위해 CommentResponseDto 형태로 반환
     */
    @PostMapping("/comment")
    public CommentResponseDto createComment(@RequestBody CommentRequestDto requestDto, HttpServletRequest req) {
        User user = (User) req.getAttribute("user");

        return commentService.createComment(requestDto, user.getUsername());
    }

    /**
     * 클라이언트에게 받은 메모 ID를 바탕으로 메모를 조회하고, 조회된 메모를 클라이언트가 제공한 수정 내용으로 수정
     * @param id 클라이언트가 요청한 수정할 메모의 ID
     * @param contents 수정할 댓글 내용
     * @param req 필터에서 저장한 토큰의 유저 정보를 가져오기 위한 HttpServletRequest
     * @return 수정한 댓글을 반환형식에 맞게 CommentResponseDto 를 만들어 반환
     */
    @PutMapping("/comment/{id}")
    public CommentResponseDto updateComment(@PathVariable Long id, @RequestBody Map<String,String> contents, HttpServletRequest req) {

        User user = (User) req.getAttribute("user");

        return commentService.updateComment(id, contents.get("contents"), user.getUsername(), user.getRole());
    }

    /**
     * 클라이언트에게 받은 메모 ID를 바탕으로 메모를 조회하고, 조회한 메모를 삭제
     * @param id 클라이언트가 요청한 삭제할 메모의 ID
     * @param req 필터에서 저장한 토큰의 유저 정보를 가져오기 위한 HttpServletRequest
     * @return 삭제한 댓글을 반환형식에 맞게 CommentResponseDto 를 만들어 반환
     */

    @DeleteMapping("/comment/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, HttpServletRequest req) {
        User user = (User) req.getAttribute("user");
        return commentService.deleteComment(id, user.getUsername(), user.getRole());
    }
}