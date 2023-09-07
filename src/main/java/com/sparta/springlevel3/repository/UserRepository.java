package com.sparta.springlevel3.repository;


import com.sparta.springlevel3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User 테이블을 관리하는 Repository
 */
public interface UserRepository extends JpaRepository<User, Long> {
   /**
    * 사용자 이름으로 사용자 정보를 조회하는 메서드
    * @param username 사용자 이름
    * @return 사용자 정보
    */
   Optional<User> findByUsername(String username);

}
