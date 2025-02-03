package com.example.nbe233team9.domain.hospital.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.example.nbe233team9.domain.hospital.dto.AnimalHospitalDTO
import com.example.nbe233team9.domain.hospital.model.AnimalHospital
import com.example.nbe233team9.domain.hospital.repository.AnimalHospitalRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@Service
class AnimalHospitalService(
    private val animalHospitalRepository: AnimalHospitalRepository,
    private val coordinateConverter: CoordinateConverter
) {
    private val restTemplate = RestTemplate()

    @Value("\${KAKAO_CLIENT_ID}")
    private lateinit var kakaoClientId: String

    @Value("\${API_KEY}")
    private lateinit var apiKey: String

    @Value("\${API_URI}")
    private lateinit var apiUri: String

    @Transactional(readOnly = true)
    fun searchHospitalsByGuAndDong(gu: String?, dong: String?): List<AnimalHospitalDTO> {
        val hospitals = when {
            gu != null && dong != null -> animalHospitalRepository.findByRdnWhlAddrContainingAndSiteWhlAddrContaining(gu, dong)
            gu != null -> animalHospitalRepository.findByRdnWhlAddrContaining(gu)
            else -> animalHospitalRepository.findBySiteWhlAddrContaining(dong ?: "")
        }

        return hospitals.map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun searchHospitalsByKeyword(keyword: String): List<AnimalHospitalDTO> {
        val hospitals = animalHospitalRepository.findByRdnWhlAddrContainingOrSiteWhlAddrContaining(keyword, keyword)
        return hospitals.map { it.toDto() }
    }

    fun fetchAndSaveCoordinates() {
        val hospitals = animalHospitalRepository.findAll()
        hospitals.forEach { hospital ->
            val address = hospital.rdnWhlAddr?.split(",")?.firstOrNull()?.trim() ?: return@forEach // ,(쉼표) 이후의 문자열(주소)까지 카카오맵 api에서 검색을 하기때문에 쉼표 이전까지의 문자열검색으로 수정.

            val url = "https://dapi.kakao.com/v2/local/search/address.json?query=$address"
            val headers = HttpHeaders().apply { set("Authorization", "KakaoAK $kakaoClientId") }
            val entity = HttpEntity<String>(headers)

            try {
                val response: ResponseEntity<String> = restTemplate.exchange(url, HttpMethod.GET, entity, String::class.java)
                val root: JsonNode = ObjectMapper().readTree(response.body)
                val documents = root["documents"]

                if (documents != null && documents.size() > 0) {
                    val location = documents[0]
                    hospital.latitude = location["y"].asDouble()
                    hospital.longitude = location["x"].asDouble()
                    animalHospitalRepository.save(hospital)
                } else {
                    println("좌표를 찾을 수 없는 주소: $address")
                }
            } catch (e: Exception) {
                println("좌표를 가져오는 중 오류 발생: ${e.message}")
            }
        }
    }

    fun fetchAndSaveData() {
        val url = "$apiUri/$apiKey/json/LOCALDATA_020301/1/1000"
        val response = restTemplate.getForObject(url, AnimalHospitalApiResponse::class.java)

        response?.localdata020301?.row?.filter { isValidHospital(it) }?.forEach { dto ->
            try {
                val (x, y) = dto.xCode?.toDoubleOrNull() to dto.yCode?.toDoubleOrNull()
                if (x != null && y != null) {
                    val (lat, lon) = coordinateConverter.convertEPSG5179ToWGS84(x, y)
                    dto.latitude = lat
                    dto.longitude = lon
                    animalHospitalRepository.save(dto.toEntity())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun isValidHospital(dto: AnimalHospitalDTO): Boolean {
        val state = dto.trdStateNm ?: return false
        if (state.contains(Regex(".*(폐업|취소|말소|만료|정지|중지).*"))) return false
        return !dto.xCode.isNullOrBlank() && !dto.yCode.isNullOrBlank()
    }

    fun searchHospitals(): List<AnimalHospital> = animalHospitalRepository.findByBplcNmContaining("동물병원")

    fun findHospitalsNearLocation(latitude: Double, longitude: Double): List<AnimalHospital> {
        return animalHospitalRepository.findAll().filter {
            try {
                val distance = calculateDistance(latitude, longitude, it.latitude, it.longitude)
                distance <= 3.0
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val EARTH_RADIUS = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        return EARTH_RADIUS * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }

    fun searchByRdnWhlAddr(address: String): List<AnimalHospital> = animalHospitalRepository.findByRdnWhlAddrContaining(address)
    fun searchBySiteWhlAddr(address: String): List<AnimalHospital> = animalHospitalRepository.findBySiteWhlAddrContaining(address)

    private fun AnimalHospital.toDto() = AnimalHospitalDTO(
        opnsfTeamCode, mgtNo, apvPermYmd, trdStateGbn, trdStateNm,
        siteTel, siteWhlAddr, rdnWhlAddr, bplcNm, uptaeNm, xCode, yCode, latitude, longitude
    )
}
