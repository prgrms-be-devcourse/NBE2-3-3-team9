package com.example.nbe233team9.common.dto

data class PageMetaDTO(
    val page: Int,
    val size: Int,
    val totalItems: Long
) {
    val totalPages: Int = kotlin.math.ceil(totalItems.toDouble() / size).toInt()
    val hasNext: Boolean = page < totalPages
    val hasPrevious: Boolean = page > 1
}
