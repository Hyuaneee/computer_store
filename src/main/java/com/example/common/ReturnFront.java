package com.example.common;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;


/**
 * 通用返回结果
 *
 * @param <T>
 */
@Data
public class ReturnFront<T> {

    private Integer code; //编码：1成功，0和其它数字为失败

    private String message; //错误信息

    private T data; //数据

    private Object key; //存放键值对对象
    private Object value;

    public ReturnFront() {

    }

    //传输键值对对象
    public ReturnFront(Object key, Object value) {
        this.key = key;
        this.value = value;
    }


    //业务操作成功
    public static <T> ReturnFront<T> success(T object) {
        ReturnFront<T> exceptionR = new ReturnFront<T>();
        exceptionR.data = object;
        exceptionR.code = 1;
        return exceptionR;
    }


    //业务操作失败
    public static <T> ReturnFront<T> error(String message) {
        ReturnFront<T> exceptionR = new ReturnFront<T>();
        exceptionR.message = message;
        exceptionR.code = 0;
        return exceptionR;
    }


}
