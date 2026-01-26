package com.star.reward.shared.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    SUCCESS("200", "操作成功"),
    PARAM_ERROR("400", "参数错误"),
    UNAUTHORIZED("401", "未授权"),
    TOKEN_INVALID("1002", "Token无效"),
    TOKEN_EXPIRED("1003", "Token已过期"),
    FORBIDDEN("403", "禁止访问"),
    NOT_FOUND("404", "资源不存在"),
    BUSINESS_ERROR("500", "业务异常"),
    SYSTEM_ERROR("500", "系统异常");

    private final String code;
    private final String message;
}
