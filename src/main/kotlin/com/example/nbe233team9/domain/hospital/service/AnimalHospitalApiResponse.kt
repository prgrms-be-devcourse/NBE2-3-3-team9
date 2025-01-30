package com.example.nbe233team9.domain.hospital.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.example.nbe233team9.domain.hospital.dto.AnimalHospitalDTO

data class AnimalHospitalApiResponse(
    @JsonProperty("LOCALDATA_020301")
    val localdata020301: Localdata020301? = null
) {
    data class Localdata020301(
        @JsonProperty("list_total_count")
        val listTotalCount: Int = 0,

        @JsonProperty("RESULT")
        val result: Result? = null,

        @JsonProperty("row")
        val row: List<AnimalHospitalDTO>? = null
    )

    data class Result(
        @JsonProperty("CODE")
        val code: String? = null,

        @JsonProperty("MESSAGE")
        val message: String? = null
    )
}