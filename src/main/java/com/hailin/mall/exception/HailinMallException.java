package com.hailin.mall.exception;

public class HailinMallException extends RuntimeException{
    private final Integer code;
    private final String msg;

    public HailinMallException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public HailinMallException(HailinMallExceptionEnum exceptionEnum){
        this(exceptionEnum.getCode(), exceptionEnum.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
