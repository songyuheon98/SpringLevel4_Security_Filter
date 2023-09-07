package com.sparta.springlevel3.service;

import com.sparta.springlevel3.dto.*;
import com.sparta.springlevel3.entity.Memo;
import com.sparta.springlevel3.entity.OnlyMemo;
import com.sparta.springlevel3.entity.UserRoleEnum;
import com.sparta.springlevel3.repository.CommentRepository;
import com.sparta.springlevel3.repository.MemoRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.LinkedList;
import java.util.List;

/**
 * 메모 관련 비즈니스 로직 처리를 담당하는 서비스
 */
@Service// 빈으로 등록
public class MemoService {

    private final MemoRepository memoRepository; // final은 무조건 생성자로 주입
    private final CommentRepository commentRepository;

    private HttpServletResponse httpServletResponse;
    public MemoService(MemoRepository memoRepository, CommentRepository commentRepository) {
        this.memoRepository = memoRepository;
        this.commentRepository = commentRepository;
    }


    /**
     * 클라이언트에게 받은 정보를 바탕으로 메모를 생성하고, 생성된 메모 반환형식에 맞게 MemoResponseDto 를 만들어 반환
     * @param requestDto 클라이언트가 작성할 메모 정보를 담은 DTO
     * @param username  필터에서 저장한 토큰의 유저 정보를 가져오기 위한 HttpServletRequest
     * @return 생성된 메모에 대한 정보를 클라이언트에게 알려주기 위해 MemoResponseDto 형태로 반환
     */
    public MemoResponseDto createMemo(MemoRequestDto requestDto, String username) {
        // RequestDto -> Entity
        Memo memo = new Memo(requestDto, username);
        // DB 저장

        /**
         * MemoRepository.save() 메소드를 사용하여 Memo 객체를 DB에 저장
         */
        Memo saveMemo = memoRepository.save(memo);
        // Entity -> ResponseDto
        MemoResponseDto memoResponseDto = new MemoResponseDto(saveMemo);
        return memoResponseDto;
    }

    /**
     * DB에 저장된 모든 메모와 댓글들을 Client에게 반환
     * @return
     */
    public List<MemoCommentResponseAllDto> getMemos() {
        /**
         * 메모와 댓글들을 저장할 MemoCommentResponseAllDto 리스트 생성
         */
        List<MemoCommentResponseAllDto> memoCommentResponseAllDtoList = new LinkedList<>();

        /**
         * MemoRepository.findAllByOrderById() 메소드를 사용하여 DB에 저장된 모든 메모를 가져옴
         * forEach문을 사용해서 메모를 하나씩 꺼내 그에 해당하는 댓글들을 가져와 MemoCommentResponseAllDto에 저장
         * MemoCommentResponseAllDtoList에 MemoCommentResponseAllDto를 추가
         * MemoCommentResponseAllDtoList를 반환
         */
        memoRepository.findAllByOrderById().stream() // stream 메소드를 통해 MemoResponseDto로 변환
                .forEach(
                        memo -> { // memo MemoResponseDto로 변환
                            MemoCommentDto temp = new MemoCommentDto(memo,
                                    commentRepository.findAllByPostidOrderById(memo.getId()).stream()
                                            .map(n->new CommentResponseDto().fromComment(n)).toList());
                            memoCommentResponseAllDtoList.add(new MemoCommentResponseAllDto(temp));

                        }

                );// 메모를 리스트 타입으로 반환
        return memoCommentResponseAllDtoList;
    }


    /**
     * 클라이언트에게 받은 메모 ID를 바탕으로 메모를 조회하고, 조회된 메모와 댓글들을 반환형식에 맞게 MemoCommentResponseAllDto 를 만들어 반환
     * @param id 클라이언트가 요청한 메모의 ID
     * @return 조회된 메모와 그에 담긴 댓글을 반환형식에 맞게 MemoCommentResponseAllDto로 반환
     */
    public MemoCommentResponseAllDto getOneMemo(Long id) {
        /**
         * 메모와 댓글들을 저장할 MemoCommentResponseAllDto 리스트 생성
         */
        MemoCommentResponseAllDto memoCommentResponseAllDtoList;

        /**
         * MemoRepository.findMemoById() 메소드를 사용하여 DB에 저장된 메모 중 클라이언트가 요청한 메모를 가져옴
         */
        Memo tempMemo = memoRepository.findMemoById(id);

        /**
         * MemocommentDto에 메모와 그에 담긴 댓글들을 저장
         */
        MemoCommentDto temp = new MemoCommentDto(tempMemo,commentRepository.findAllByPostidOrderById(tempMemo.getId()).stream()
                        .map(n->new CommentResponseDto().fromComment(n)).toList());

        /**
         * MemoCommentResponseAllDto에 MemoCommentDto를 저장
         */
        memoCommentResponseAllDtoList =new MemoCommentResponseAllDto(temp);

        /**
         * MemoCommentResponseAllDto를 반환
         */
        return memoCommentResponseAllDtoList;
    }

    /**
     * 클라이언트에게 받은 메모 ID를 바탕으로 메모를 수정하고, 수정된 메모를 반환형식에 맞게 MemoResponseDto 를 만들어 반환
     * @param id 클라이언트가 요청한 수정할 메모의 ID
     * @param requestDto 클라이언트가 보낸 메모 수정 정보를 담은 DTO
     * @param username 필터에서 저장한 토큰의 유저 정보를 가져오기 위해 사용
     * @param role 필터에서 저장한 토큰의 유저 정보를 가져오기 위해 사용
     * @return 수정된 메모를 반환형식에 맞게 MemoResponseDto 를 만들어 반환
     */
    @Transactional // updateMemo는 따로 Transactional 되어있지 않아 해줘야함
    public OnlyMemo updateMemo(Long id, MemoRequestDto requestDto, String username, UserRoleEnum role) {

        Memo memo;
        try {
            memo = findMemo(id);
        } catch (IllegalArgumentException e) {
            return new OnlyMemo("400","선택한 메모는 존재하지 않습니다.");
        }

        /**
         * 수정하려는 메모의 작성자와 수정하려는 유저가 같은 경우 혹은 관리자 권한을 가진 경우에만 메모 수정 가능
         */
        if(role.getAuthority().equals("ROLE_ADMIN")|| memo.getUsername().equals(username) )
            memo.update(requestDto, memo.getUsername()); // update는 memo 클래스에서 만든 것
        else {
            /**
             * 수정하려는 메모의 작성자와 수정하려는 유저가 다른 경우나 관리자 권한을 가지지 않은 경우 메모 수정 불가
             */
            return new OnlyMemo("400","당신에겐 글을 수정할 권한이 없습니다 >.< !!");
        }
        /**
         * 수정된 메모 내용을 반환
         */
        return new OnlyMemo(memo);

    }

    public ResponseEntity<String>  deleteMemo(Long id, String username, UserRoleEnum role) {
        Memo memo;
        try {
            memo = findMemo(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"msg\": \"삭제 실패\", \"statusCode\": 400}");
        }
        if(role.getAuthority().equals("ROLE_ADMIN")|| memo.getUsername().equals(username))
            memoRepository.delete(memo);
        else
            return ResponseEntity.badRequest().body("{\"msg\": \"삭제 실패\", \"statusCode\": 400}");
        return ResponseEntity.ok("{\"msg\": \"삭제 성공\", \"statusCode\": 200}");

    }

    private Memo findMemo(Long id) { // 메모 찾기
        return memoRepository.findById(id).orElseThrow(() ->  // null 시 오류 메시지 출력
                new IllegalArgumentException("선택한 메모는 존재하지 않습니다.")
        );
    }
}