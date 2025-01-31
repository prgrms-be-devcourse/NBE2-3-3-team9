package com.example.nbe233team9.domain.hospital.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.hospital.dto.AnimalHospitalDTO
import com.example.nbe233team9.domain.hospital.model.AnimalHospital
import com.example.nbe233team9.domain.hospital.service.AnimalHospitalService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "animal hospital", description = "동물병원 API")
@RestController
@RequestMapping
class AnimalHospitalController(private val animalHospitalService: AnimalHospitalService) {

    @Operation(summary = "공공 API 호출", description = "공공 API 데이터를 받아옵니다.")
    @GetMapping("/api/animal-hospitals")
    fun importData(): String {
        animalHospitalService.fetchAndSaveData()
        return ResponseEntity.ok(ApiResponse.ok("데이터 수집 및 저장 완료")).toString()
    }

    @Operation(summary = "좌표 변환", description = "좌표를 변환합니다. EPSG:5179 형식(국내에서 흔히 사용되는 TM 좌표계)일 경우, 이를 EPSG:4326 형식(세계 표준 위경도 좌표계)")
    @PostMapping("/api/update-coordinates")
    fun updateCoordinates(): String {
        animalHospitalService.fetchAndSaveCoordinates()
        return ResponseEntity.ok(ApiResponse.ok("좌표수정 완료")).toString()

    }

    @Operation(summary = "주변 동물병원 검색", description = "주변 동물병원 검색을 하는 API 입니다. 요청 항목 : latitude, longitude")
    @GetMapping("/api/animal-hospitals/nearby")
    fun findNearbyHospitals(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double
    ): ResponseEntity<ApiResponse<List<AnimalHospital>>> {
        val nearbyHospitals = animalHospitalService.findHospitalsNearLocation(latitude, longitude)
        return ResponseEntity.ok(ApiResponse.ok(nearbyHospitals))
    }

    @Operation(summary = "동물병원 단어 검색", description = "동물병원을 검색 하는 API 입니다. 요청 항목: keyword 동물병원")
    @GetMapping("/api/animal-hospitals/namesearch")
    fun searchHospitals(@RequestParam(required = false) keyword: String?): ResponseEntity<Any> {
        if (keyword != "동물병원" || keyword.isNullOrEmpty()) {
            return ResponseEntity.ok(emptyList<Any>())
        }
        val searchResults = animalHospitalService.searchHospitals()
        return ResponseEntity.ok(ApiResponse.ok(searchResults))
    }

    @Operation(summary = "동물병원 주소 동, 구, 키워드 검색", description = "동물병원 주소 동, 구, 키워드 검색 API 입니다. 요청 항목 : **동(dong), **구(gu), 키워드(keyword)")
    @GetMapping("/api/animal-hospitals/dongorgu")
    fun searchHospitalsByDongOrGu(
        @RequestParam(required = false) gu: String?,
        @RequestParam(required = false) dong: String?,
        @RequestParam(required = false) keyword: String?
    ): ResponseEntity<ApiResponse<List<AnimalHospitalDTO>>> {
        val result = when {
            gu != null || dong != null -> animalHospitalService.searchHospitalsByGuAndDong(gu, dong)
            !keyword.isNullOrBlank() -> animalHospitalService.searchHospitalsByKeyword(keyword)
            else -> emptyList()
        }
        return ResponseEntity.ok(ApiResponse.ok(result))
    }
}