package com.dzhy.manage.exception;

/**
 * @ClassName GeneralException
 * @Description 通用异常
 * @Author alex
 * @Date 2018/10/30
 **/
public class GeneralException extends RuntimeException {
    public GeneralException(String msg) {
        super(msg);
    }
}
