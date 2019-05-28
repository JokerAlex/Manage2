package com.dzhy.manage.interceptor;

import com.dzhy.manage.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @ClassName SignInterceptor
 * @Description 签名验证
 * @Author alex
 * @Date 2019-05-28
 **/
@Service
@Slf4j
public class SignInterceptor implements HandlerInterceptor {

    @Value("${spring.profiles.active}")
    private String env;

    private static final String SECRET = "4bef7d19dc9843fa89c7d74f7c0053c7";
    private static final String ENV = "dev";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        request.setAttribute("startTime", System.currentTimeMillis());
        String params = this.getParams(request);
        if (!ENV.equals(env)) {
            checkSign(params, request);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        long startTime = (long) request.getAttribute("startTime");
        log.info("spend time :{}", System.currentTimeMillis() - startTime);
    }

    private String getParams(HttpServletRequest request) {
        List<String> keys = new ArrayList<>(request.getParameterMap().keySet());
        keys.remove("sign");

        Collections.sort(keys);
        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            builder.append(key).append("=").append(request.getParameter(key)).append("&");
        }
        log.info("IP:{}, HTTP_METHOD:{}, URL:{}, ARGS:{}",
                request.getHeader("X-Real-IP"),
                request.getMethod(),
                request.getRequestURI(),
                builder.toString());

        builder.append("secret").append("=").append(SECRET);
        return builder.toString();
    }

    private void checkSign(String params, HttpServletRequest request) {
        String timestamp = request.getParameter("timestamp");
        if (StringUtils.isNotEmpty(timestamp)
                && Math.abs(Long.parseLong(timestamp) - System.currentTimeMillis()) > 300000) {
            log.info("timestamp error, timestamp:{}, now:{}", timestamp, System.currentTimeMillis());
            throw new GeneralException("time error");
        }
        String requestSign = request.getParameter("sign");
        String sign = DigestUtils.md5Hex(params);
        if (!StringUtils.equals(requestSign, sign)) {
            log.info("sign error, requestSign:{}, sign:{}", requestSign, sign);
            throw new GeneralException("sign error");
        }
    }
}
