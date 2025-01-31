package com.example.nbe233team9.domain.community.controller

import com.example.nbe233team9.common.dto.PageDTO
import com.example.nbe233team9.common.dto.PageRequestDTO
import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.community.dto.AnimalSpeciesDTO
import com.example.nbe233team9.domain.community.dto.CommunityRequestDTO
import com.example.nbe233team9.domain.community.dto.CommunityResponseDTO
import com.example.nbe233team9.domain.community.dto.DetailResponseDTO
import com.example.nbe233team9.domain.community.service.CommunityService
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/community")
class CommunityController(
    private val communityService: CommunityService
) {

    @GetMapping
    fun showPosts(
        pageRequestDTO: PageRequestDTO,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) category: String?
    ): ApiResponse<PageDTO<CommunityResponseDTO>> {
        val pageDTO = communityService.showPosts(pageRequestDTO, keyword, category)
        return ApiResponse.ok(pageDTO)
    }

    @GetMapping("/species")
    fun getAnimalSpecies(): ApiResponse<AnimalSpeciesDTO> {
        val animalSpecies = communityService.getDistinctAnimalSpecies()
        return ApiResponse.ok(animalSpecies)
    }

    @GetMapping("/myPost")
    @PreAuthorize("hasRole('USER')")
    fun showMyPosts(@AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<List<CommunityResponseDTO>> {
        val posts = communityService.showMyPosts(userDetails.getUserId())
        return ApiResponse.ok(posts)
    }

    @GetMapping("/{postingId}")
    fun showPostDetail(@PathVariable postingId: Long): ApiResponse<DetailResponseDTO> {
        val detailResponseDTO = communityService.showPostDetail(postingId)
        return ApiResponse.ok(detailResponseDTO)
    }

    @PostMapping("/post", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('USER')")
    fun createPost(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestPart(value = "dto") communityRequestDTO: CommunityRequestDTO,
        @RequestPart(value = "file", required = false) file: MultipartFile?
    ): ApiResponse<CommunityResponseDTO> {
        val communityResponseDTO = communityService.createPost(userDetails.getUserId(), communityRequestDTO, file)
        return ApiResponse.created(communityResponseDTO)
    }

    @PutMapping("/post/{postingId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('USER')")
    fun updatePost(
        @PathVariable postingId: Long,
        @RequestPart(value = "dto") communityRequestDTO: CommunityRequestDTO,
        @RequestPart(value = "file", required = false) file: MultipartFile?
    ): ApiResponse<CommunityResponseDTO> {
        val communityResponseDTO = communityService.updatePost(postingId, communityRequestDTO, file)
        return ApiResponse.ok(communityResponseDTO)
    }

    @DeleteMapping("/post/{postingId}")
    @PreAuthorize("hasRole('USER')")
    fun deletePost(@PathVariable postingId: Long): ApiResponse<String> {
        communityService.deletePost(postingId)
        return ApiResponse.ok("글 삭제 완료")
    }
}