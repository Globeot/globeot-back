    package com.globeot.globeotback.global.exception;

    import org.springframework.http.HttpStatus;

    public enum ErrorCode {

        // Common
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 내부 오류가 발생했습니다."),
        BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
        UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
        FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "접근 권한이 없습니다."),

        // Auth
        INVALID_SCHOOL_EMAIL(HttpStatus.BAD_REQUEST, "AUTH4001", "학교 메일이 아닙니다."),
        EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH4002", "이미 가입된 이메일입니다."),
        USER_RECENTLY_DELETED(HttpStatus.BAD_REQUEST, "AUTH4003", "탈퇴 후 30일이 지나지 않았습니다."),
        OTP_SEND_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "AUTH4004", "인증번호 발송 횟수를 초과했습니다."),
        OTP_TOO_FAST(HttpStatus.BAD_REQUEST, "AUTH4005", "잠시 후 다시 요청해주세요."),
        OTP_NOT_REQUESTED(HttpStatus.BAD_REQUEST, "AUTH4006", "인증번호 요청이 없습니다."),
        OTP_BLOCKED(HttpStatus.BAD_REQUEST, "AUTH4007", "인증 시도가 너무 많습니다."),
        OTP_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH4008", "인증 시간이 만료되었습니다."),
        OTP_FAIL_THREE_TIMES(HttpStatus.BAD_REQUEST, "AUTH4009", "인증번호 3회 실패"),
        OTP_MISMATCH(HttpStatus.BAD_REQUEST, "AUTH4010", "인증번호가 일치하지 않습니다."),
        EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "AUTH4011", "이메일 인증이 필요합니다."),
        NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH4012", "이미 사용 중인 닉네임입니다."),
        LOGIN_FAILED(HttpStatus.BAD_REQUEST, "AUTH4013", "이메일 또는 비밀번호가 올바르지 않습니다."),
        USER_DELETED(HttpStatus.BAD_REQUEST, "AUTH4014", "탈퇴한 계정입니다."),
        PASSWORD_RESET(HttpStatus.BAD_REQUEST, "AUTH4015", "비밀번호가 초기화되었습니다."),

        // Email
        EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "EMAIL5001", "이메일 전송에 실패했습니다."),

        // User
        USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER4001", "사용자를 찾을 수 없습니다."),
        INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "USER4002", "이메일 또는 비밀번호가 올바르지 않습니다."),
        DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "USER4003", "이미 사용중인 이메일입니다."),
        UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "USER4004", "인증 정보가 없습니다."),
        CANNOT_WITHDRAW_ACTIVE_ARTICLE(HttpStatus.BAD_REQUEST, "USER4005", "진행 중인 게시글이 있어 탈퇴할 수 없습니다."),
        CANNOT_WITHDRAW_PENDING_REPORT(HttpStatus.BAD_REQUEST, "USER4006", "신고 처리 중인 게시글이 있어 탈퇴할 수 없습니다."),

        // School
        SCHOOL_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL4001", "등록되지 않은 학교입니다."),
        FAVORITE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "FAVORITE4001", "이미 관심 학교로 등록된 학교입니다."),
        FAVORITE_NOT_FOUND(HttpStatus.NOT_FOUND, "FAVORITE4002", "관심 학교로 등록되지 않은 학교입니다."),

        // Application
        GPA_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "APPLICATION4001", "학점 증빙 이미지는 필수입니다."),
        ENGLISH_SCORE_IMAGE_REQUIRED(HttpStatus.BAD_REQUEST, "APPLICATION4002", "어학 성적표 이미지는 필수입니다."),
        APPLICATION_SCHOOL_REQUIRED(HttpStatus.BAD_REQUEST, "APPLICATION4003", "학교 최소 1개 선택은 필수입니다."),
        APPLICATION_PRIORITY1_REQUIRED(HttpStatus.BAD_REQUEST, "APPLICATION4004", "1순위 학교 선택은 필수입니다."),
        APPLICATION_ALREADY_SUBMITTED(HttpStatus.BAD_REQUEST, "APPLICATION4005", "이미 지원 내역을 제출한 사용자입니다."),
        APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATION4006", "지원 정보를 찾을 수 없습니다."),
        APPLICATION_PENDING(HttpStatus.BAD_REQUEST, "APPLICATION4007", "지원서가 아직 처리 중입니다."),

        // S3
        S3_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S35001", "파일 업로드에 실패했습니다.");

        private final HttpStatus status;
        private final String code;
        private final String message;

        ErrorCode(HttpStatus status, String code, String message) {
            this.status = status;
            this.code = code;
            this.message = message;
        }

        public HttpStatus getStatus() {
            return status;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }