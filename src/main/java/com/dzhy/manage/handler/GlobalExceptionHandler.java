package com.dzhy.manage.handler;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @ClassName GlobalExceptionHandler
 * @Description 全局异常处理
 * @Author alex
 * @Date 2019-05-18
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result handlerException(Exception e) {
        log.error(e.getMessage());
        return Result.isError("服务器错误");
    }

    @ExceptionHandler(GeneralException.class)
    public Result handlerGeneralException(GeneralException e) {
        log.error(e.getMessage());
        return Result.isError(e.getMessage());
    }
}
