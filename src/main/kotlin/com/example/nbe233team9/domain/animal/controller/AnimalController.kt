package com.example.nbe233team9.domain.animal.controller

import com.example.nbe233team9.common.response.ApiResponse
import com.example.nbe233team9.domain.animal.dto.CreateAnimalDTO
import com.example.nbe233team9.domain.animal.dto.FindBreedDTO
import com.example.nbe233team9.domain.animal.dto.FindSpeciesDTO
import com.example.nbe233team9.domain.animal.service.AnimalService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RequestMapping("/api")
@RestController
class AnimalController(
    val animalService: AnimalService
) {
    @PostMapping("/animal")
    fun createAnimal(@RequestBody createAnimalDTO: CreateAnimalDTO): ApiResponse<String> {
        animalService.createAnimal(createAnimalDTO)
        return ApiResponse.ok("생성 완료")
    }

    @GetMapping("/species")
    fun findSpecies(): ApiResponse<List<FindSpeciesDTO>> {
        val findSpeciesDTOs: List<FindSpeciesDTO> = animalService.findSpecies()
        return ApiResponse.ok(findSpeciesDTOs)
    }

    @GetMapping("/breeds/{speciesId}")
    fun findBreeds(@PathVariable speciesId: Long): ApiResponse<List<FindBreedDTO>> {
        val findBreedDTOs: List<FindBreedDTO> = animalService.findBreedsBySpecies(speciesId)
        return ApiResponse.ok(findBreedDTOs)
    }
}