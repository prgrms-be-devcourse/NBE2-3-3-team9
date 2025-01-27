package com.example.nbe233team9.common.dto

import lombok.AllArgsConstructor

@AllArgsConstructor
data class PageDTO<T>(
    val data: List<T>,
    val meta: PageMetaDTO
)
