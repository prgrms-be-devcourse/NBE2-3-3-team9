package com.example.nbe233team9.domain.pet.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.pet.dto.PetDTO
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class PetController {
    fun findPets(): ApiResponse<List<PetDTO>> {
        return ApiResponse.ok(null);
    }

    fun getPetDetails(): ApiResponse<PetDTO> {
        return ApiResponse.ok(null);
    }

    fun addPets(): ApiResponse<PetDTO> {
        return ApiResponse.ok(null);
    }

    fun updatePets(): ApiResponse<PetDTO> {
        return ApiResponse.ok(null);
    }

    fun deletePets() {

    }
}
