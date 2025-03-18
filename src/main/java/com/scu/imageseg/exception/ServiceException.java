package com.scu.imageseg.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException{
    private Integer code;

    /**
     * 自定义错误类型,枚举中没有的错误类型
     * @param code 自定义的错误码
     * @param msg 自定义的错误提示
     */
    public ServiceException(Integer code, String msg){
        super(msg);
        this.code = code;
    }
}
