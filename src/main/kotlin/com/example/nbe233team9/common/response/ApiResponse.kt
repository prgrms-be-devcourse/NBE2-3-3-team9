package com.example.nbe233team9.common.response

import com.example.nbe233team9.common.exception.CustomException
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.http.HttpStatus


data class ApiResponse<T>(
    @JsonIgnore val httpStatus: HttpStatus,
    val success: Boolean,
    val data: T? = null,
    val errorMessage: String? = null
) {
    companion object {
        fun <T> ok(data: T?): ApiResponse<T> {
            return ApiResponse(httpStatus = HttpStatus.OK, success = true, data = data)
        }

        fun <T> created(data: T?): ApiResponse<T> {
            return ApiResponse(httpStatus = HttpStatus.CREATED, success = true, data = data)
        }

        fun <T> fail(e: CustomException): ApiResponse<T> {
            return ApiResponse(
                httpStatus = e.resultCode.httpStatus,
                success = false,
                data = null,
                errorMessage = e.resultCode.message
            )
        }
    }
}
