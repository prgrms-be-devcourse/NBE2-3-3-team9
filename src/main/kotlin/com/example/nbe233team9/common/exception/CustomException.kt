package com.example.nbe233team9.common.exception

class CustomException(
    val resultCode: ResultCode
) : RuntimeException(resultCode.message)