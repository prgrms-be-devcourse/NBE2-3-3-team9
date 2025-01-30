package com.example.nbe233team9.domain.information.controller

import com.example.nbe233team9.common.dto.PageDTO
import com.example.nbe233team9.common.dto.PageRequestDTO
import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.information.dto.InformationRequestDTO
import com.example.nbe233team9.domain.information.dto.InformationResponseDTO
import com.example.nbe233team9.domain.information.service.InformationService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/information")
class InformationController(
    private val informationService: InformationService
) {

    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun saveInformation(
        @RequestPart(value = "dto") informationDTO: InformationRequestDTO,
        @RequestPart(value = "file", required = false) file: MultipartFile?
    ): ApiResponse<String> {
        informationService.saveInformation(informationDTO, file)
        return ApiResponse.ok("동물 정보 등록 성공")
    }

    @GetMapping
    fun getInformation(
        pageRequestDTO: PageRequestDTO,
        @RequestParam(required = false) speciesName: String?,
        @RequestParam(required = false) breedName: String?
    ): ApiResponse<PageDTO<InformationResponseDTO>> {
        val pageDTO = informationService.getInformation(pageRequestDTO, speciesName, breedName)
        return ApiResponse.ok(pageDTO)
    }

    @GetMapping("/{informationId}")
    fun getInformationDetail(@PathVariable informationId: Long): ApiResponse<InformationResponseDTO> {
        val informationDTO = informationService.getInformationDetail(informationId)
        return ApiResponse.ok(informationDTO)
    }
}