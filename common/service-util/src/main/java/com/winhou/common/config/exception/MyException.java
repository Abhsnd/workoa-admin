package com.winhou.common.config.exception;

import com.winhou.common.result.ResultCodeEnum;
import lombok.Data;

@Data
public class MyException extends RuntimeException{
    private  Integer code;
    private String msg;

    public MyException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 接收枚举类型对象
     * @param resultCodeEnum
     */
    public MyException(ResultCodeEnum resultCodeEnum) {
        super(resultCodeEnum.getMessage());
        this.code = resultCodeEnum.getCode();
        this.msg = resultCodeEnum.getMessage();
    }

    @Override
    public String toString() {
        return "UserException{" +
                "code=" + code +
                ", message=" + this.getMessage() +
                '}';
    }




}
