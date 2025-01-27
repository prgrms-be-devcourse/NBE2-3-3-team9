package com.example.nbe233team9.common.exception

import org.springframework.http.HttpStatus

enum class ResultCode(
    val httpStatus: HttpStatus,
    val message: String
) {
    // 성공
    SUCCESS(HttpStatus.OK, "성공"),

    // 클라이언트 요청 관련
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다."),
    UNSUPPORTED_HTTP_METHOD(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "필수 요청 파라미터가 누락되었습니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "잘못된 JSON 형식입니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    ACCESS_DENIED_ADMIN(HttpStatus.FORBIDDEN, "해당 리소스에 관리자 권한이 필요합니다."),
    ACCESS_DENIED_USER(HttpStatus.FORBIDDEN, "해당 리소스에 사용자 권한이 필요합니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "권한이 없습니다."),

    // 유저 관련
    NOT_EXISTS_USER(HttpStatus.NOT_FOUND, "해당 유저가 존재하지 않습니다."),
    FAIL_TO_SAVE_USER(HttpStatus.INTERNAL_SERVER_ERROR, "유저 정보 저장에 실패했습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 등록된 이메일입니다."),
    INVALID_USER_PASSWORD(HttpStatus.BAD_REQUEST, "유효하지 않은 비밀번호입니다."),
    USER_ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "사용자 계정이 잠겼습니다."),

    // 반려동물 관련
    NOT_EXISTS_PET(HttpStatus.NOT_FOUND, "반려동물을 등록하지 않았습니다."),
    INVALID_GENDER_VALUE(HttpStatus.BAD_REQUEST, "올바르지 않은 성별입니다"),

    // 종 관련
    NOT_EXISTS_SPECIES(HttpStatus.NOT_FOUND, "해당하는 종이 없습니다."),
    DUPLICATE_SPECIES(HttpStatus.CONFLICT, "이미 존재하는 종입니다"),
    DUPLICATE_SPECIES_AND_BREED(HttpStatus.CONFLICT, "이미 존재하는 종과 품종입니다"),
    NOT_EXISTS_BREED(HttpStatus.NOT_FOUND, "종에 해당되는 품종이 등록되어 있지 않습니다"),

    // 동물 정보 관련
    NOT_EXISTS_INFORMATION(HttpStatus.NOT_FOUND, "해당하는 정보가 없습니다"),
    DUPLICATE_INFORMATION(HttpStatus.CONFLICT, "이미 작성한 정보입니다"),

    // 스케줄 관련
    NOT_EXISTS_SCHEDULE(HttpStatus.NOT_FOUND, "요청하신 스케줄이 없습니다."),
    INVALID_DATETIME_VALUE(HttpStatus.BAD_REQUEST, "올바르지 않은 날짜 입력입니다"),

    // 커뮤니티 관련
    NOT_EXISTS_POST(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."),
    NOT_EXISTS_COMMENT(HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다"),
    DUPLICATE_LIKE(HttpStatus.CONFLICT, "이미 좋아요를 누른 상태입니다"),
    NOT_EXISTS_LIKE(HttpStatus.NOT_FOUND, "좋아요를 누르지 않은 상태입니다"),

    // 파일 관련
    EMPTY_FILE_NAME(HttpStatus.BAD_REQUEST, "파일 이름이 비었습니다"),
    INVALID_FILE_EXTENSION(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "알맞지 않은 파일 형식입니다"),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드를 실패했습니다"),

    // 서버 내부 관련
    DB_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB오류 입니다."),
    ETC_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 이유로 실패했습니다.")
}
