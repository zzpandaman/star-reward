package com.star.reward.shared.exception;

import com.star.common.result.ResultCode;
import lombok.Getter;

/**
 * 系统异常
 */
@Getter
public class SystemException extends RuntimeException {

    private final int code;

    public SystemException(String message) {
        super(message);
        this.code = ResultCode.SYSTEM_ERROR.getCode();
    }

    public SystemException(int code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResultCode.SYSTEM_ERROR.getCode();
    }
}
