package com.example.common;

import lombok.Data;


/**
 * 通用返回结果  用于前端对接
 *
 * @param <T>
 */
@Data
public class Result<T> {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String message; //错误信息

    private T data; //数据

    private Object key; //存放键值对对象
    private Object value;

    public Result() {

    }

    //传输键值对对象
    public Result(Object key, Object value) {
        this.key = key;
        this.value = value;
    }


    //业务操作成功
    public static <T> Result<T> success(T object) {
        Result<T> exceptionR = new Result<T>();
        exceptionR.data = object;
        exceptionR.code = 1;
        return exceptionR;
    }

    //业务操作失败
    public static <T> Result<T> error(String message) {
        Result<T> exceptionR = new Result<T>();
        exceptionR.message = message;
        exceptionR.code = 0;
        return exceptionR;
    }


}
