package com.example.nbe233team9.domain.hospital.repository

import com.example.nbe233team9.domain.hospital.model.AnimalHospital
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnimalHospitalRepository : JpaRepository<AnimalHospital, String> {
    // 동물 병원 이름 검색
    fun findByBplcNmContaining(hospitalName: String): List<AnimalHospital>

    // 도로명 주소 검색 (rdn_whl_addr)
    fun findByRdnWhlAddrContaining(rdnWhlAddr: String): List<AnimalHospital>

    // 일반 주소 검색 (site_whl_addr)
    fun findBySiteWhlAddrContaining(siteWhlAddr: String): List<AnimalHospital>

    // 단어 검색
    fun findByRdnWhlAddrContainingOrSiteWhlAddrContaining(
        rdnWhlAddrKeyword: String,
        siteWhlAddrKeyword: String
    ): List<AnimalHospital>

    fun findByRdnWhlAddrContainingAndSiteWhlAddrContaining(
        rdnWhlAddr: String,
        siteWhlAddr: String
    ): List<AnimalHospital>
}
