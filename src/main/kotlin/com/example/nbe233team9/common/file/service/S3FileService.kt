package com.example.nbe233team9.common.file.service

import com.example.nbe233team9.common.exception.CustomException
import com.example.nbe233team9.common.exception.ResultCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.ObjectCannedACL
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.*

@Service
class S3FileService(
    private val s3Client: S3Client,
    @Value("\${spring.cloud.aws.s3.bucket}")
    private val bucket: String
) {

    fun uploadFile(multipartFile: MultipartFile, dirName: String): String {
        val originalFileName = multipartFile.originalFilename
            ?: throw CustomException(ResultCode.EMPTY_FILE_NAME)

        if (!isValidExtension(originalFileName)) {
            throw CustomException(ResultCode.INVALID_FILE_EXTENSION)
        }

        val uniqueFileName = "${UUID.randomUUID()}_${originalFileName.replace("\\s+".toRegex(), "_")}"
        val fileName = "$dirName/$uniqueFileName"

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(fileName)
            .acl(ObjectCannedACL.PUBLIC_READ)
            .contentType(multipartFile.contentType)
            .build()

        s3Client.putObject(
            putObjectRequest,
            RequestBody.fromInputStream(multipartFile.inputStream, multipartFile.size)
        )

        return s3Client.utilities().getUrl { builder ->
            builder.bucket(bucket).key(fileName)
        }.toExternalForm()
    }

    fun deleteFile(fileName: String) {
        val splitFilename = ".com/"
        val originalFileName = fileName.substring(fileName.lastIndexOf(splitFilename) + splitFilename.length)
        val decodedFileName = URLDecoder.decode(originalFileName, StandardCharsets.UTF_8)

        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(decodedFileName)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }

    fun updateFile(newFile: MultipartFile, oldFileName: String?, dirName: String): String {
        if (!oldFileName.isNullOrEmpty()) {
            deleteFile(oldFileName)
        }
        return uploadFile(newFile, dirName)
    }

    private fun isValidExtension(originalFileName: String): Boolean {
        val fileExtension = originalFileName.substringAfterLast(".").lowercase(Locale.getDefault())
        val allowedExtensions = listOf("jpg", "jpeg", "png", "gif")
        return allowedExtensions.contains(fileExtension)
    }
}
