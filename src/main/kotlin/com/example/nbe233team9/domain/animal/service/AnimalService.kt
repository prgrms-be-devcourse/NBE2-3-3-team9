package com.example.nbe233team9.domain.animal.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.domain.animal.dto.CreateAnimalDTO
import com.example.nbe233team9.domain.animal.dto.FindBreedDTO
import com.example.nbe233team9.domain.animal.dto.FindSpeciesDTO
import com.example.nbe233team9.domain.animal.model.Breed
import com.example.nbe233team9.domain.animal.model.Species
import com.example.nbe233team9.domain.animal.repository.BreedRepository
import com.example.nbe233team9.domain.animal.repository.SpeciesRepository
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class AnimalService(
    val speciesRepository: SpeciesRepository,
    val breedRepository: BreedRepository
) {
    fun createAnimal(createAnimalDTO: CreateAnimalDTO) {
        var species: Species? = speciesRepository.findByName(createAnimalDTO.speciesName)

        // 만약 품종을 입력했다면
        if (createAnimalDTO.breedName != null) {
            if (species != null) {
                // 이미 종과 품종 존재
                if (breedRepository.existsByNameAndSpecies(createAnimalDTO.breedName, species)) {
                    throw CustomException(ResultCode.DUPLICATE_SPECIES_AND_BREED)
                } else { // 종만 존재 -> 품종만 추가
                    val breed = Breed(
                        species = species!!,
                        name = createAnimalDTO.breedName!!
                    )

                    breedRepository.save(breed)
                }
            } else { // 모두 존재X -> 종, 품종 추가
                species = Species(
                    name = createAnimalDTO.speciesName
                )

                speciesRepository.save(species!!)

                val breed: Breed = Breed(
                    species = species!!,
                    name = createAnimalDTO.breedName!!
                )

                breedRepository.save(breed)
            }
        } else {
            if (species == null) {
                species = Species(
                    name = createAnimalDTO.speciesName
                )

                speciesRepository.save(species!!)
            } else {
                throw CustomException(ResultCode.DUPLICATE_SPECIES)
            }
        }
    }

    fun findSpecies(): List<FindSpeciesDTO> {
        val lists = speciesRepository.findAll()

        if (lists.isEmpty()) {
            throw CustomException(ResultCode.NOT_EXISTS_SPECIES)
        }

        val findSpeciesDTOs: List<FindSpeciesDTO> = lists.map { species: Species ->
            FindSpeciesDTO.fromEntity(species)
        }

        return findSpeciesDTOs
    }

    fun findBreedsBySpecies(speciesId: Long): List<FindBreedDTO> {
        val species = speciesRepository.findById(speciesId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_SPECIES) }

        val lists = breedRepository.findBreedsBySpecies(species)
        if (lists.isEmpty()) {
            throw CustomException(ResultCode.NOT_EXISTS_BREED)
        }

        val findBreedDTOs: List<FindBreedDTO> = lists.map { breed: Breed ->
                FindBreedDTO.fromEntity(breed)
            }

        return findBreedDTOs
    }
}