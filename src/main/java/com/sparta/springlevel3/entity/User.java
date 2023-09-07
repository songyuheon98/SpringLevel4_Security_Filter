package com.sparta.springlevel3.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원 정보를 담은 Entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    /**
     * Enumerated(EnumType.STRING) 를 사용하면,
     * Enum의 이름을 DB에 저장할 수 있다.
     */
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRoleEnum role;

    /**
     *
     * @param username
     * @param password
     * @param role
     */
    public User(String username, String password, UserRoleEnum role) { // 아이디는 자동증가임
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
