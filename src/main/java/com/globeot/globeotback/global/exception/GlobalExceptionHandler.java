package com.globeot.globeotback.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 잘못된 요청 (회원가입 중복, 비밀번호 틀림 등)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e
    ) {

        ErrorResponse response = new ErrorResponse(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // 서버 에러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e,
            HttpServletRequest request // ✅ 추가
    ) {

        String uri = request.getRequestURI();

        // 🔥🔥🔥 Swagger는 예외 처리하지 말고 그대로 던지기
        if (uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui")) {
            throw new RuntimeException(e);
        }

        ErrorResponse response =
                new ErrorResponse("서버 내부 오류가 발생했습니다.");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}