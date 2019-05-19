package com.dzhy.manage.utils;

import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.security.entity.JwtUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;

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

    public static int getDateToIntNow() {
        LocalDate time = LocalDate.now();
        String s = time.toString().replace("-", "");
        return Integer.valueOf(s);
    }

    public static int getDateToIntOf(int year, int month, int date) {
        LocalDate time = LocalDate.of(year, month, date);
        String s = time.toString().replace("-", "");
        return Integer.valueOf(s);
    }

    public static int getMonthToIntOf(int year, int month) {
        LocalDate time = LocalDate.of(year, month, 1);
        String monthStr = time.toString().substring(0, time.toString().lastIndexOf("-"));
        String s = monthStr.replace("-", "");
        return Integer.valueOf(s);
    }

    public static void main(String[] args) {
        System.out.println(getDateToIntNow());
        System.out.println(getDateToIntOf(2019, 5, 19));
        System.out.println(getMonthToIntOf(2019, 5));
    }
}
