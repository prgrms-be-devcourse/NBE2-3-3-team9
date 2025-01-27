package com.example.nbe233team9.common.dto

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort

data class PageRequestDTO(
    var page: Int = 1, // 기본 페이지 번호
    var size: Int = 3, // 한 페이지 당 크기
    var sortBy: String = "id",
    var direction: Sort.Direction = Sort.Direction.DESC
) {
    fun toPageRequest(): PageRequest {
        return PageRequest.of(
            page - 1,
            size,
            Sort.by(direction, sortBy)
        )
    }
}