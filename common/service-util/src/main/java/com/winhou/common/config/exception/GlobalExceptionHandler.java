package com.winhou.common.config.exception;

import com.winhou.common.result.Result;
import com.winhou.common.result.ResultCodeEnum;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @Author wiho
 * @Date 2023/4/30 12:51
 * @Description 全局异常处理类
 * @Since version-1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 全局异常
    @ExceptionHandler(Exception.class)
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.fail().message("执行全局异常处理...");
    }

    // 特定异常
    @ExceptionHandler(ArithmeticException.class)
    public Result error(ArithmeticException e) {
        e.printStackTrace();
        return Result.fail().message("执行特定异常处理...");
    }

    // SpringSecurity没有权限异常处理
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Result error(AccessDeniedException e) throws AccessDeniedException{
        return Result.build(null, ResultCodeEnum.PERMISSION);
    }

    // 自定义异常
    @ExceptionHandler(MyException.class)
    public Result error(MyException e) {
        e.printStackTrace();
        return Result.fail().code(e.getCode()).message(e.getMsg());
    }
}
