package com.sparta.springlevel3.service;

import com.sparta.springlevel3.dto.CommentRequestDto;
import com.sparta.springlevel3.dto.CommentResponseDto;
import com.sparta.springlevel3.entity.Comment;
import com.sparta.springlevel3.entity.UserRoleEnum;
import com.sparta.springlevel3.repository.CommentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 댓글 관련 비즈니스 로직 처리를 담당하는 서비스
 */
@Service// 빈으로 등록
public class CommentService {

    /**
     * 생성자: CommentService 클래스가 생성될 때 스프링에서 자동으로 실행됨
     */
    private final CommentRepository commentRepository; // final은 무조건 생성자로 주입
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * 클라이언트에게 받은 정보를 바탕으로 댓글을 생성하고, 생성된 댓글 반환형식에 맞게 CommentResponseDto 를 만들어 반환
     * @param requestDto 클라이언트가 작성할 댓글 정보를 담은 DTO
     * @param username  필터에서 저장한 토큰의 유저 정보를 가져오기 위한 HttpServletRequest
     * @return 생성된 댓글에 대한 정보를 클라이언트에게 알려주기 위해 CommentResponseDto 형태로 반환
     */
    public CommentResponseDto createComment(CommentRequestDto requestDto, String username) {
        // RequestDto -> Entity
        Comment comment = new Comment(requestDto, username);
        // DB 저장
        Comment saveComment = commentRepository.save(comment);
        // Entity -> ResponseDto
        CommentResponseDto commentResponseDto = new CommentResponseDto(saveComment);
        return commentResponseDto;
    }

    /**
     * 클라이언트에게 받은 메모 ID를 바탕으로 메모를 조회하고, 조회된 메모와 댓글들을 반환형식에 맞게 MemoCommentResponseAllDto 를 만들어 반환
     * @param id 클라이언트가 요청한 메모의 ID
     * @param contents 수정할 댓글 내용
     * @param username 필터에서 저장한 토큰의 유저 정보를 가져오기 위한 HttpServletRequest
     * @param role 필터에서 저장한 토큰의 유저 권한 정보를 가져오기 위해 필요한 UserRoleEnum
     * @return 작성한 댓글을 반환형식에 맞게 MemoCommentResponseAllDto 를 만들어 반환
     */
    @Transactional // updateMemo는 따로 Transactional 되어있지 않아 해줘야함
    public CommentResponseDto updateComment(Long id, String contents, String username, UserRoleEnum role) {
        Comment comment = findComment(id);
        if(role.getAuthority().equals("ROLE_ADMIN")|| comment.getUsername().equals(username) )
            comment.update(contents); // update는 memo 클래스에서 만든 것
        else
            throw new IllegalArgumentException("당신에겐 글을 수정할 권한이 없습니다 >.< !!");

        return new CommentResponseDto().fromComment(comment);
    }

    /**
     * 클라이언트에게 받은 메모 ID를 바탕으로 메모를 삭제
     * @param id 클라이언트가 요청한 삭제할 메모의 ID
     * @param username 필터에서 저장한 토큰의 유저 정보를 가져오기 위한 HttpServletRequest
     * @param role 필터에서 저장한 토큰의 유저 권한 정보를 가져오기 위해 필요한 UserRoleEnum
     * @return 삭제 성공 시 삭제 성공 메시지 및 Status Code 200 반환
     */
    public ResponseEntity<String> deleteComment(Long id, String username, UserRoleEnum role) {
        Comment comment = findComment(id);
        if(role.getAuthority().equals("ROLE_ADMIN")|| comment.getUsername().equals(username))
            commentRepository.delete(comment);
        else
            return ResponseEntity.ok("{\"msg\": \"댓글 삭제 실패\", \"statusCode\": 444}");
        return ResponseEntity.ok("{\"msg\": \"댓글 삭제 성공\", \"statusCode\": 200}");

    }

    /**
     * 댓글 ID 를 바탕으로 댓글을 조회
     * @param id 클라이언트가 요청한 댓글의 ID
     * @return 조회된 댓글
     */
    private Comment findComment(Long id) { // 메모 찾기
        return commentRepository.findById(id).orElseThrow(() ->  // null 시 오류 메시지 출력
                new IllegalArgumentException("선택한 댓글은 존재하지 않습니다.")
        );

    }



}