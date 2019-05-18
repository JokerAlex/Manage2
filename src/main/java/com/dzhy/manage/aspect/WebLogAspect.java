package com.dzhy.manage.aspect;

import com.dzhy.manage.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @ClassName WebLogAspect
 * @Description 请求日志
 * @Author alex
 * @Date 2019-05-18
 **/
@Component
@Aspect
@Slf4j
public class WebLogAspect {

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(public * com.dzhy.manage.controller..*Controller(..))")
    public void webLog() {}


    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();

        // 记录下请求内容
        log.info("Last-Skip-IP:{}, HTTP_METHOD:{}, URL:{}, CLASS_METHOD:{}, ARGS:{}",
                request.getHeader("X-Real-IP"),
                request.getMethod(),
                request.getRequestURI(),
                joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs()));
        startTime.set(System.currentTimeMillis());

    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        // 处理完请求，返回内容
        log.info("RESPONSE:{}, SPEND TIME:{}", ((Result)ret).isOk(), (System.currentTimeMillis() - startTime.get()));
        startTime.remove();
    }
}
