package com.globeot.globeotback.global.exception;

import com.globeot.globeotback.global.response.ApiResponse;
import tools.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.onFailure("COMMON400", message));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestPartException(MissingServletRequestPartException e) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.onFailure("COMMON400", "필수 요청 데이터가 누락되었습니다: " + e.getRequestPartName()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        ErrorCode errorCode = ErrorCode.FILE_SIZE_EXCEEDED;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.onFailure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        String message = "요청 본문을 읽을 수 없습니다. JSON 형식을 확인해주세요.";

        InvalidFormatException ife = findCause(e, InvalidFormatException.class);
        if (ife != null && ife.getTargetType() != null && ife.getTargetType().isEnum()) {
            String allowedValues = java.util.Arrays.stream(ife.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            Object badValue = ife.getValue();
            message = String.format("값 '%s'은(는) 유효하지 않습니다. 허용값: [%s]", badValue, allowedValues);
        }

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.onFailure("COMMON400", message));
    }

    @SuppressWarnings("unchecked")
    private <T extends Throwable> T findCause(Throwable e, Class<T> type) {
        Throwable c = e;
        while (c != null) {
            if (type.isInstance(c)) return (T) c;
            c = c.getCause();
        }
        return null;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName();
        String message;
        Class<?> targetType = e.getRequiredType();

        if (targetType != null && targetType.isEnum()) {
            String allowedValues = java.util.Arrays.stream(targetType.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            message = String.format("파라미터 '%s'의 값이 유효하지 않습니다. 허용값: [%s]", paramName, allowedValues);
        } else {
            message = String.format("파라미터 '%s'의 값 형식이 올바르지 않습니다.", paramName);
        }

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.onFailure("COMMON400", message));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.onFailure("COMMON400", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.onFailure("COMMON400", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e, HttpServletRequest request) {

        String uri = request.getRequestURI();

        if (uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui")) {
            throw new RuntimeException(e);
        }

        e.printStackTrace();

        return ResponseEntity
                .internalServerError()
                .body(ApiResponse.onFailure("COMMON500", "서버 내부 오류가 발생했습니다."));
    }
}