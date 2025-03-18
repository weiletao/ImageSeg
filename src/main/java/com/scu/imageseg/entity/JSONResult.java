package com.scu.imageseg.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 封装数据JSON工具类
 *
 */
@Data // 此处使用@Data注解是为了让Springboot以为是这个对象是数据库实体类 以便直接返回JSON格式的数据
public class JSONResult<T> implements Serializable {
    private T data;
    private Integer code;
    private String message;

    public JSONResult() {

    }

    /**
     * 若没有数据返回，可以人为指定状态码和提示信息,一般用于异常信息
     * @param code
     * @param msg
     */
    public JSONResult(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    /**
     * 有数据返回时，状态码为200，默认提示信息为：操作成功！
     * @param data
     */
    public JSONResult(T data) {
        this.data = data;
        this.code = 200;
        this.message = "操作成功！";
    }

    /**
     * 有数据返回，状态码为200，人为指定提示信息
     * @param data
     * @param msg
     */
    public JSONResult(T data, String msg) {
        this.data = data;
        this.code = 200;
        this.message = msg;
    }

    /**
     * 有数据返回，人为指定状态码，人为指定提示信息
     * @param data
     * @param msg
     * @param code
     */
    public JSONResult(T data, String msg, Integer code){
        this.data = data;
        this.code = code;
        this.message = msg;
    }
}
