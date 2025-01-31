package com.example.nbe233team9.domain.pet.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.auth.security.CustomUserDetails
import com.example.nbe233team9.domain.pet.dto.PetDTO
import com.example.nbe233team9.domain.pet.service.PetService
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
class PetController(
    private val petService: PetService
) {
    @GetMapping("/pets")
    @PreAuthorize("hasRole('USER')")
    fun findPets(@AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<List<PetDTO>> {
        val pets = petService.findPets(userDetails.getUserId());
        return ApiResponse.ok(pets);
    }

    @GetMapping("/pets/{petId}")
    @PreAuthorize("hasRole('USER')")
    fun getPetDetails(@PathVariable petId: Long,
                      @AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<PetDTO> {
        val pet = petService.getPetDetails(userDetails.getUserId(), petId)
        return ApiResponse.ok(pet);
    }

    @PostMapping(value = ["/pet"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('USER')")
    fun addPets(@RequestPart(value = "dto" ) request: PetDTO.AddPetDTO,
                @RequestPart(value = "file", required = false ) file: MultipartFile?,
                @AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<PetDTO> {
        val pet = petService.addPet(request, file, userDetails.getUserId())
        return ApiResponse.ok(pet);
    }

    @PutMapping(value = ["/pet/{petId}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasRole('USER')")
    fun updatePets(@RequestPart(value = "dto" ) request: PetDTO.UpdatePetDTO,
                   @RequestPart(value = "file", required = false ) file: MultipartFile?,
                   @AuthenticationPrincipal userDetails: CustomUserDetails): ApiResponse<PetDTO> {
        val pet = petService.updatePet(request, file, userDetails.getUserId())
        return ApiResponse.ok(pet);
    }

    @DeleteMapping("/pet/{petId}")
    fun deletePets(@PathVariable petId: Long): ApiResponse<String> {
        petService.deletePet(petId)
        return ApiResponse.ok("펫 삭제 성공")
    }
}
