package com.example.nbe233team9.common.exception

import com.example.nbe233team9.common.response.ApiResponse
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ApiResponse<*> {
        return ApiResponse.fail<Void>(e)
    }

    // JSON 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ApiResponse<Void> {
        return ApiResponse.fail(CustomException(ResultCode.INVALID_JSON))
    }

    // HTTP 메서드 오류
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ApiResponse<Void> {
        return ApiResponse.fail(CustomException(ResultCode.UNSUPPORTED_HTTP_METHOD))
    }

    // 요청 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(ex: MissingServletRequestParameterException): ApiResponse<Map<String, String>> {
        val errors = mapOf("error" to ex.message.orEmpty())

        return ApiResponse(ResultCode.MISSING_PARAMETER.httpStatus, false, errors, ResultCode.MISSING_PARAMETER.message)
    }

    // Validation 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ApiResponse<Map<String, String>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage.orEmpty() }

        return ApiResponse(ResultCode.INVALID_REQUEST.httpStatus, false, errors, ResultCode.INVALID_REQUEST.message)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ApiResponse<Map<String, String>> {
        val errors = mapOf("error" to ex.message.orEmpty())

        return ApiResponse(ResultCode.INVALID_REQUEST.httpStatus, false, errors, ResultCode.INVALID_REQUEST.message)
    }
}