package com.example.rebookauthservice.common.exception;

import com.rebook.common.core.exception.BusinessException;
import com.rebook.common.core.exception.ErrorCode;

/**
 * Auth 도메인 예외
 * BusinessException을 상속받아 Auth 서비스의 모든 예외를 통합 처리
 */
public class AuthException extends BusinessException {

    public AuthException(ErrorCode code) {
        super(code);
    }

    // 편의 메서드들
    public static AuthException unknown() {
        return new AuthException(ErrorCode.UNKNOWN_ERROR);
    }

    public static AuthException unknown(String message) {
        return new AuthException(ErrorCode.UNKNOWN_ERROR);
    }
}
