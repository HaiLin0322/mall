package com.hailin.mall.exception;

import com.hailin.mall.common.ApiRestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

//处理统一异常的handle
@ControllerAdvice
public class GlobalExceptionHandle {
    private final Logger log= LoggerFactory.getLogger(GlobalExceptionHandle.class);
    @ExceptionHandler(Exception.class)//规定处理的异常类型
    @ResponseBody
    public Object handleException(Exception e){
        log.error("Default Exception:",e);//打印异常
        return ApiRestResponse.error(HailinMallExceptionEnum.SYSTEM_ERROR);
    }
    @ExceptionHandler(HailinMallException.class)//规定处理的异常类型
    @ResponseBody
    public Object handleHailinMallException(HailinMallException e){
        log.error("HailinMallException:",e);
        return ApiRestResponse.error(e.getCode(),e.getMsg());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)//规定处理的异常类型.参数不合法异常
    @ResponseBody
    public ApiRestResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException:",e);
        return handleBindingResult(e.getBindingResult());
    }
    private ApiRestResponse handleBindingResult(BindingResult result){
        //把异常处理为对外暴露的提示
        List<String> list=new ArrayList<>();
        if (result.hasErrors()) {
            List<ObjectError> allErrors=result.getAllErrors();
            for (ObjectError objectError : allErrors) {
                String defaultMessage = objectError.getDefaultMessage();//获取错误信息
                list.add(defaultMessage);
            }
        }
        if (list.size()==0){
            return ApiRestResponse.error(HailinMallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(HailinMallExceptionEnum.REQUEST_PARAM_ERROR.getCode(),list.toString());
    }
}
