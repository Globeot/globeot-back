package com.globeot.globeotback.global.exception;

import java.time.LocalDateTime;

public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final LocalDateTime rejoinAvailableAt;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.rejoinAvailableAt = null;
    }

    public CustomException(ErrorCode errorCode, LocalDateTime rejoinAvailableAt) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.rejoinAvailableAt = rejoinAvailableAt;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public LocalDateTime getRejoinAvailableAt() {
        return rejoinAvailableAt;
    }
}