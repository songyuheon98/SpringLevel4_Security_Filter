package com.sparta.springlevel3.entity;

/**
 * 사용자 권한을 설정해 주기 위한 Enum
 */
public enum UserRoleEnum {

    //UserRoleEnum role = UserRoleEnum.USER;
    USER(Authority.USER),  // 사용자 권한
    ADMIN(Authority.ADMIN);  // 관리자 권한

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    /**
     * 사용자의 권한을 나타내는 문자열
     * Spring Security에서는 권한을 "ROLE_"로 시작해야 한다.
     */
    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}