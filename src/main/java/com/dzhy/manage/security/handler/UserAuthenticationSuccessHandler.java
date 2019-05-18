package com.dzhy.manage.security.handler;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.security.entity.JwtUserDetails;
import com.dzhy.manage.security.entity.TokenResult;
import com.dzhy.manage.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserAuthenticationSuccessHandler
 * @Description Success Handler
 * @Author alex
 * @Date 2019-05-18
 **/
@Component
@Slf4j
public class UserAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;

    @Autowired
    public UserAuthenticationSuccessHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        JwtUserDetails jwtUserDetails = (JwtUserDetails) authentication.getPrincipal();
        UserInfo userInfo = jwtUserDetails.getUserInfo();
        userInfo.setPassword(null);

        Map<String, Object> claimsMap = new HashMap<>(3);
        claimsMap.put("userId", userInfo.getUserId());
        claimsMap.put("username", userInfo.getUsername());
        claimsMap.put("name", userInfo.getName());

        String token = JwtUtil.createJwt(claimsMap);

        TokenResult tokenResponse = new TokenResult(token, userInfo);
        log.info("login success username = {}", userInfo.getUsername());
        response.setHeader("Content-type", "application/json;charset=utf-8");
        objectMapper.writeValue(response.getWriter(), Result.isSuccess(tokenResponse));
    }
}
