package com.sparta.springlevel3.security;

import com.sparta.springlevel3.entity.User;
import com.sparta.springlevel3.entity.UserRoleEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Spring Security에서 사용자의 정보를 담당하는 클래스
 * UserDetails 인터페이스를 구현하였다.
 */

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 사용자의 권한을 리턴하는 함수
     * @return 사용자의 권한 리턴 (ROLE_USER, ROLE_ADMIN)
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /**
         * 사용자의 권한을 UserRoleEnum 형태로 가져온다.
         */
        UserRoleEnum role = user.getRole();

        /**
         * authority의 문자열 값 (ROLE_USER, ROLE_ADMIN)를 authorities에 담는다.
         */
        String authority = role.getAuthority();

        /**
         * authority 값을 통해 SimpleGrantedAuthority 객체를 생성한다.
         *
         */
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);

        /**
         * GrantedAuthority 타입의 객체를 담는 리스트 생성 한다.
         */
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        /**
         * 리스트에 SimpleGrantedAuthority 객체를 담는다.
         */
        authorities.add(simpleGrantedAuthority);

        /**
         * 사용자에게 부여 받은 권한들의 리스트를 리턴한다.
         */
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}