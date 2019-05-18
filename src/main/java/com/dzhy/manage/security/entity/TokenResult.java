package com.dzhy.manage.security.entity;

import com.dzhy.manage.entity.UserInfo;
import lombok.Data;

/**
 * @ClassName TokenResult
 * @Description token response
 * @Author alex
 * @Date 2019-05-18
 **/
@Data
public class TokenResult {
    private String token;

    private UserInfo userInfo;

    public TokenResult(String token, UserInfo userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }
}
