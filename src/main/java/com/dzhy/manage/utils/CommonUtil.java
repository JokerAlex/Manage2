package com.dzhy.manage.utils;

import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.security.entity.JwtUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @ClassName CommonUtil
 * @Description 通用工具
 * @Author alex
 * @Date 2019-05-18
 **/
public class CommonUtil {
    private CommonUtil() {}

    public static UserInfo getUserInfoFromContext() {
        JwtUserDetails details = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return details == null ? null : details.getUserInfo();
    }

    public static Integer getUserIdFromContext() {
        UserInfo userInfo = getUserInfoFromContext();
        return userInfo == null ? null : userInfo.getUserId();
    }
}
