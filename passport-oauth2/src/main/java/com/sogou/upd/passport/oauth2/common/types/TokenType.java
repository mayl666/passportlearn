package com.sogou.upd.passport.oauth2.common.types;

/**
 * 根据access_token访问API时的两种传输类型
 * Bearer和MAC
 */
public enum TokenType {
    BEARER("Bearer"),
    MAC("MAC");

    private String tokenType;

    TokenType(String grantType) {
        this.tokenType = grantType;
    }

    @Override
    public String toString() {
        return tokenType;
    }
}
