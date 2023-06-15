package com.winhou.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {
    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),
    DATA_ERROR(204, "数据异常"),

    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),
    LOGIN_ERROR(208, "认证失败");

    private Integer code;       // 状态代码
    private String message;     // 状态信息

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
