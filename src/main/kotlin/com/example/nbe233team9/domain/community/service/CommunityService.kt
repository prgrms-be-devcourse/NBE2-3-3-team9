package com.example.nbe233team9.domain.community.service

import com.example.nbe233team9.common.dto.PageDTO
import com.example.nbe233team9.common.dto.PageMetaDTO
import com.example.nbe233team9.common.dto.PageRequestDTO
import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import com.example.nbe233team9.common.file.service.S3FileService
import com.example.nbe233team9.domain.community.dto.*
import com.example.nbe233team9.domain.community.model.Community
import com.example.nbe233team9.domain.community.repository.CommentRepository
import com.example.nbe233team9.domain.community.repository.CommunityLikeRepository
import com.example.nbe233team9.domain.community.repository.CommunityRepository
import com.example.nbe233team9.domain.user.repository.UserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException


@Service
class CommunityService(
    private val communityRepository: CommunityRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val communityLikeRepository: CommunityLikeRepository,
    private val s3FileService: S3FileService
) {

    fun showPosts(pageRequestDTO: PageRequestDTO, keyword: String?, category: String?): PageDTO<CommunityResponseDTO> {
        val pageRequest = pageRequestDTO.toPageRequest()

        val communityPage = when {
            keyword != null && category != null ->
                communityRepository.searchByKeyWordAndCategory(keyword, category, pageRequest)
            keyword != null ->
                communityRepository.searchByKeyWord(keyword, pageRequest)
            category != null ->
                communityRepository.searchByCategory(category, pageRequest)
            else ->
                communityRepository.findAll(pageRequest)
        }

        val posts = communityPage.content.map { CommunityResponseDTO.fromEntity(it) }
        val meta = PageMetaDTO(pageRequestDTO.page, pageRequestDTO.size, communityPage.totalElements)

        return PageDTO(posts, meta)
    }

    fun getDistinctAnimalSpecies(): AnimalSpeciesDTO =
        AnimalSpeciesDTO(communityRepository.findDistinctAnimalSpecies())

    fun showMyPosts(userId: Long): List<CommunityResponseDTO> =
        communityRepository.findByUserId(userId).map { CommunityResponseDTO.fromEntity(it) }

    fun showPostDetail(postingId: Long): DetailResponseDTO {
        val community = communityRepository.findById(postingId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_POST) }

        val communityResponseDTO = CommunityResponseDTO.fromEntity(community)

        val authentication: Authentication? = SecurityContextHolder.getContext().authentication
        val userId = authentication?.takeIf { it.isAuthenticated }?.name

        val liked = userId?.takeIf { it != "anonymousUser" }?.toLongOrNull()
            ?.let { communityLikeRepository.existsByCommunityIdAndUserId(community.id!!, it) } ?: false

        communityResponseDTO.liked = liked

        val comments = commentRepository.findByCommunityIdAndParentIsNull(postingId).map { CommentResponseDTO.fromEntity(it) }

        return DetailResponseDTO(communityResponseDTO, comments)
    }

    fun createPost(userId: Long, communityRequestDTO: CommunityRequestDTO, file: MultipartFile?): CommunityResponseDTO {
        val user = userRepository.findById(userId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_USER) }

        val pictureUrl = try {
            file?.takeIf { !it.isEmpty }?.let { s3FileService.uploadFile(it, "community") }
        } catch (e: IOException) {
            throw CustomException(ResultCode.FILE_UPLOAD_ERROR)
        }

        val community = Community(
            user = user,
            title = communityRequestDTO.title,
            content = communityRequestDTO.content,
            picture = pictureUrl,
            animalSpecies = communityRequestDTO.animalSpecies
        )

        communityRepository.save(community)

        return CommunityResponseDTO.fromEntity(community)
    }

    fun updatePost(postingId: Long, communityRequestDTO: CommunityRequestDTO, file: MultipartFile?): CommunityResponseDTO {
        val community = communityRepository.findById(postingId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_POST) }

        community.updatePost(communityRequestDTO)

        try {
            file?.takeIf { !it.isEmpty }?.let {
                community.updatePicture(s3FileService.updateFile(it, community.picture, "community"))
            }
        } catch (e: IOException) {
            throw CustomException(ResultCode.FILE_UPLOAD_ERROR)
        }

        communityRepository.save(community)
        return CommunityResponseDTO.fromEntity(community)
    }

    fun deletePost(postingId: Long) {
        val community = communityRepository.findById(postingId)
            .orElseThrow { CustomException(ResultCode.NOT_EXISTS_POST) }

        community.picture?.let { s3FileService.deleteFile(it) }
        communityRepository.deleteById(postingId)
    }
}