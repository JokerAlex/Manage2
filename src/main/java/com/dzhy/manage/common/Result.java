package com.dzhy.manage.common;

import com.dzhy.manage.enums.ResultEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.ToString;

/**
 * @ClassName Result
 * @Description 通用结果返回
 * @Author alex
 * @Date 2019-05-17
 **/
@Getter
@ToString
public class Result<T> {
    private int status;
    private String msg;

    private T data;

    private Result(int status){
        this.status = status;
    }
    private Result(int status, T data){
        this.status = status;
        this.data = data;
    }

    private Result(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private Result(int status, String msg){
        this.status = status;
        this.msg = msg;
    }

    /**
     * 判断请求是否成功
     * 注解使之不在json序列化结果当中
     * @return
     */
    @JsonIgnore
    public boolean isOk(){
        return this.status == ResultEnum.SUCCESS.getCode();
    }

    public static <T> Result<T> isSuccess(){
        return new Result<T>(ResultEnum.SUCCESS.getCode());
    }

    public static <T> Result<T> isSuccess(String msg){
        return new Result<T>(ResultEnum.SUCCESS.getCode(),msg);
    }

    public static <T> Result<T> isSuccess(T data){
        return new Result<T>(ResultEnum.SUCCESS.getCode(),data);
    }

    public static <T> Result<T> isSuccess(String msg, T data){
        return new Result<T>(ResultEnum.SUCCESS.getCode(),msg,data);
    }


    public static <T> Result<T> isError(){
        return new Result<T>(ResultEnum.ERROR.getCode(),ResultEnum.ERROR.getMessage());
    }


    public static <T> Result<T> isError(String errorMessage){
        return new Result<T>(ResultEnum.ERROR.getCode(),errorMessage);
    }

    public static <T> Result<T> isError(int errorCode, String errorMessage){
        return new Result<T>(errorCode,errorMessage);
    }
}
