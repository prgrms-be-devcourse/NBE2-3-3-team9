package com.example.nbe233team9.domain.information.service

import com.example.nbe233team9.common.dto.PageDTO
import com.example.nbe233team9.common.dto.PageMetaDTO
import com.example.nbe233team9.common.dto.PageRequestDTO
import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.common.file.service.S3FileService
import com.example.nbe233team9.domain.animal.repository.BreedRepository
import com.example.nbe233team9.domain.information.dto.InformationRequestDTO
import com.example.nbe233team9.domain.information.dto.InformationResponseDTO
import com.example.nbe233team9.domain.information.model.Information
import com.example.nbe233team9.domain.information.repository.InformationRepository
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


@Service
class InformationService(
    private val informationRepository: InformationRepository,
    private val breedRepository: BreedRepository,
    private val s3FileService: S3FileService
) {

    fun saveInformation(informationDTO: InformationRequestDTO, file: MultipartFile?) {
        val breed = breedRepository.findByName(informationDTO.breedName)

        val check = informationRepository.findBySpeciesAndBreed(informationDTO.speciesName, informationDTO.breedName)
        if (check != null) {
            throw CustomException(ResultCode.DUPLICATE_INFORMATION)
        }

        val pictureUrl = try {
            if (file != null && !file.isEmpty) {
                s3FileService.uploadFile(file, "community")
            } else {
                null
            }
        } catch (e: IOException) {
            throw CustomException(ResultCode.FILE_UPLOAD_ERROR)
        }

        val information = Information(
            breed = breed,
            age = informationDTO.age,
            picture = pictureUrl,
            height = informationDTO.height,
            weight = informationDTO.weight,
            guide = informationDTO.guide,
            description = informationDTO.description
        )

        informationRepository.save(information)
    }

    fun getInformation(pageRequestDTO: PageRequestDTO, speciesName: String?, breedName: String?): PageDTO<InformationResponseDTO> {
        val pageRequest = pageRequestDTO.toPageRequest()

        val informationPage: Page<Information> = when {
            (speciesName.isNullOrEmpty() && breedName.isNullOrEmpty()) -> {
                informationRepository.findAllInformation(pageRequest)
            }
            speciesName.isNullOrEmpty() -> {
                throw CustomException(ResultCode.NOT_EXISTS_SPECIES)
            }
            breedName.isNullOrEmpty() -> {
                informationRepository.findBySpeciesName(speciesName, pageRequest)
            }
            else -> {
                informationRepository.findBySpeciesAndBreed(speciesName, breedName, pageRequest)
            }
        }

        val posts = informationPage.content.map { InformationResponseDTO.fromEntity(it) }

        val meta = PageMetaDTO(pageRequestDTO.page, pageRequestDTO.size, informationPage.totalElements)

        return PageDTO(posts, meta)
    }

    fun getInformationDetail(informationId: Long): InformationResponseDTO {
        val information = informationRepository.findById(informationId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_INFORMATION) }

        information.updateHit(information.hit + 1)
        informationRepository.save(information)

        return InformationResponseDTO.fromEntity(information)
    }


}