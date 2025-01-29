package com.example.nbe233team9.domain.pet.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.common.file.service.S3FileService
import com.example.nbe233team9.domain.animal.model.Breed
import com.example.nbe233team9.domain.animal.model.Species
import com.example.nbe233team9.domain.animal.repository.BreedRepository
import com.example.nbe233team9.domain.animal.repository.SpeciesRepository
import com.example.nbe233team9.domain.pet.dto.PetDTO
import com.example.nbe233team9.domain.pet.model.Pet
import com.example.nbe233team9.domain.pet.repository.PetRepository
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@Service
class PetService(
    private val petRepository: PetRepository,
    private val userRepository: UserRepository,
    private val speciesRepository: SpeciesRepository,
    private val s3FileService: S3FileService,
    private val breedRepository: BreedRepository
) {
    fun findPets(userId: Long): List<PetDTO> {
        val lists: List<Pet> = petRepository.findAllByUserId(userId)

        if (lists.isEmpty()) {
            throw CustomException(ResultCode.NOT_EXISTS_PET)
        }
        return lists.map { pet ->
            PetDTO.fromEntity(pet)
        }
    }

    fun getPetDetails(userId: Long, petId: Long): PetDTO {
        if (!userRepository.existsById(userId)) {
            throw CustomException(ResultCode.NOT_EXISTS_USER)
        }
        if (!petRepository.existsById(petId)) {
            throw CustomException(ResultCode.NOT_EXISTS_PET)
        }
        val pet: Pet = petRepository.findById(petId).orElseThrow({ RuntimeException() })

        return PetDTO.fromEntity(pet)
    }

    fun addPet(request: PetDTO.AddPetDTO, file: MultipartFile?, userId: Long): PetDTO {
        if (!request.gender.equals("암컷") && !request.gender.equals("수컷")) {
            throw CustomException(ResultCode.INVALID_GENDER_VALUE)
        }

        val picture = try {
            file?.takeIf { !it.isEmpty }?.let { s3FileService.uploadFile(it, "pet") }
        } catch (e: IOException) {
            throw CustomException(ResultCode.FILE_UPLOAD_ERROR)
        }

        val pet = Pet(
            user = getUserById(userId),
            breed = getBreedById(request.breedId),
            species = getSpeciesById(request.speciesId),
            name = request.name,
            age = request.age,
            picture = picture,
            gender = request.gender
        )

        petRepository.save(pet)
        return PetDTO.fromEntity(pet)
    }

    fun updatePet(request: PetDTO.UpdatePetDTO, file: MultipartFile?, userId: Long): PetDTO {
        if (petRepository.findById(request.id).isEmpty) {
            throw CustomException(ResultCode.NOT_EXISTS_PET)
        }
        if (!request.gender.equals("암컷") && !request.gender.equals("수컷")) {
            throw CustomException(ResultCode.INVALID_GENDER_VALUE)
        }
        val pet = petRepository.findById(request.id)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_PET) }

        val picture = try {
            file?.takeIf { !it.isEmpty }?.let { s3FileService.uploadFile(it, "pet") }
        } catch (e: IOException) {
            throw CustomException(ResultCode.FILE_UPLOAD_ERROR)
        }

        pet.updatePet(request, speciesRepository, breedRepository, picture)

        return PetDTO.fromEntity(pet)
    }

    fun deletePet(petId: Long) {
        if (petRepository.existsById(petId)) {
            petRepository.deleteById(petId)
        } else {
            throw CustomException(ResultCode.NOT_EXISTS_PET)
        }
    }

    private fun getUserById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow { RuntimeException() }
    }

    private fun getSpeciesById(speciesId: Long): Species {
        return speciesRepository.findById(speciesId).orElseThrow { RuntimeException() }
    }

    private fun getBreedById(breedId: Long): Breed {
        return breedRepository.findById(breedId).orElseThrow { RuntimeException() }
    }
}
