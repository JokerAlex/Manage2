package com.dzhy.manage.controller;

import com.dzhy.manage.common.Result;
import com.dzhy.manage.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName GlobalExceptionHandler
 * @Description 全局异常处理
 * @Author alex
 * @Date 2019-05-18
 **/
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handlerException(Exception e) {
        log.error(e.getMessage());
        return Result.isError("服务器错误");
    }

    @ExceptionHandler(GeneralException.class)
    @ResponseBody
    public Result handlerGeneralException(GeneralException e) {
        log.error(e.getMessage());
        return Result.isError(e.getMessage());
    }
}
