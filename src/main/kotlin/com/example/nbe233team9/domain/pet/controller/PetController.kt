package com.example.nbe233team9.domain.pet.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.pet.dto.PetDTO
import com.example.nbe233team9.domain.pet.service.PetService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
class PetController(
    private val petService: PetService
) {
    @GetMapping("/pets")
    fun findPets(): ApiResponse<List<PetDTO>> {
        val pets = petService.findPets(4L);
        return ApiResponse.ok(pets);
    }

    @GetMapping("/pets/{petId}")
    fun getPetDetails(@PathVariable petId: Long): ApiResponse<PetDTO> {
        val pet = petService.getPetDetails(4L, petId)
        return ApiResponse.ok(pet);
    }

    @PostMapping(value = ["/pet"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addPets(@RequestPart(value = "dto" ) request: PetDTO.AddPetDTO,
                @RequestPart(value = "file", required = false ) file: MultipartFile?): ApiResponse<PetDTO> {
        val pet = petService.addPet(request, file, 4L)
        return ApiResponse.ok(pet);
    }

    @PutMapping(value = ["/pet/{petId}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updatePets(@RequestPart(value = "dto" ) request: PetDTO.UpdatePetDTO,
                   @RequestPart(value = "file", required = false ) file: MultipartFile?): ApiResponse<PetDTO> {
        val pet = petService.updatePet(request, file, 4L)
        return ApiResponse.ok(pet);
    }

    @DeleteMapping("/pet/{petId}")
    fun deletePets(@PathVariable petId: Long): ApiResponse<String> {
        petService.deletePet(petId)
        return ApiResponse.ok("펫 삭제 성공")
    }
}
