package com.sparta.springlevel3.repository;

import com.sparta.springlevel3.entity.Memo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Memo 테이블을 관리하는 Repository
 */
public interface MemoRepository extends JpaRepository<Memo, Long> { // JpaRepository 덕분에 @Repository 선언 안해도 자동으로 Bean으로 취급

    /**
     * 전체 메모 목록을 id 순으로 조회하는 메서드
     * @return 메모 목록
     */
    List<Memo> findAllByOrderById(); // 필요한 정보만 가져오도록 함

    /**
     * 특정 id의 메모를 조회하는 메서드
     * @param id 찾을 메모의 id
     * @return 메모
     */
    Memo findMemoById(Long id); // 아이디로 메모 찾게 하기
}