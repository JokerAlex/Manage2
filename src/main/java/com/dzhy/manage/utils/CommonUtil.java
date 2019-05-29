package com.dzhy.manage.utils;

import com.dzhy.manage.entity.UserInfo;
import com.dzhy.manage.exception.GeneralException;
import com.dzhy.manage.security.entity.JwtUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * @ClassName CommonUtil
 * @Description 通用工具
 * @Author alex
 * @Date 2019-05-18
 **/
@Slf4j
public class CommonUtil {
    private CommonUtil() {}

    public static UserInfo getUserInfoFromContext() {
        JwtUserDetails details = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return details == null ? null : details.getUserInfo();
    }

    public static Integer getUserIdFromContext() {
        UserInfo userInfo = getUserInfoFromContext();
        return userInfo == null ? -1 : userInfo.getUserId();
    }

    public static String getUserNameFromContext() {
        UserInfo userInfo = getUserInfoFromContext();
        return userInfo == null ? "未知用户" : userInfo.getName();
    }

    /**
     * 将日期转换为整型数值，例如：2019-05-20 --> 20190520
     * @return int
     */
    public static int getDateToIntNow() {
        LocalDate time = LocalDate.now();
        String s = time.toString().replace("-", "");
        return Integer.parseInt(s);
    }

    /**
     * 将日期转换为整型数值，例如：2019-05-20 --> 20190520
     * @return int
     */
    public static int getDateToIntOf(int year, int month, int date) {
        year = checkYear(year);
        month = checkMonth(month);
        date = checkDate(date);
        LocalDate time = LocalDate.of(year, month, date);
        String s = time.toString().replace("-", "");
        return Integer.parseInt(s);
    }

    /**
     * 将月份转换为整型数值，例如：2019-05 --> 201905
     * @return int
     */
    public static int getMonthToIntOf(int year, int month) {
        year = checkYear(year);
        month = checkMonth(month);
        LocalDate time = LocalDate.of(year, month, 1);
        String monthStr = time.toString().substring(0, time.toString().lastIndexOf("-"));
        String s = monthStr.replace("-", "");
        return Integer.parseInt(s);
    }

    /**
     * 将月份转换为整型数值，例如：2019-05 --> 201905
     * @return int
     */
    public static int getMonthToIntOfNow() {
        LocalDate time = LocalDate.now();
        String monthStr = time.toString().substring(0, time.toString().lastIndexOf("-"));
        String s = monthStr.replace("-", "");
        return Integer.parseInt(s);
    }

    public static String getEncoderFileName(HttpServletRequest request, String fileName) {
        String encoderFileName;
        try {
            if (request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
                encoderFileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                encoderFileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            throw new GeneralException("编码错误");
        }
        return encoderFileName;
    }

    private static int checkYear(int year) {
        if (year < 2019) {
            return 2019;
        }
        return year;
    }

    private static int checkMonth(int month) {
        if (month < 1 || month > 12) {
            return LocalDate.now().getMonthValue();
        }
        return month;
    }

    private static int checkDate(int date) {
        if (date < 1 || date > 31) {
            return LocalDate.now().getDayOfMonth();
        }
        return date;
    }

    public static void main(String[] args) {
        System.out.println(getDateToIntNow());
        System.out.println(getDateToIntOf(2019, 5, 19));
        System.out.println(getMonthToIntOf(2019, 5));
        System.out.println(getMonthToIntOfNow());
    }
}
