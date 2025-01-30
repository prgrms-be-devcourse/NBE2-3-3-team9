package com.example.nbe233team9.domain.hospital.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "animal_hospital")
data class AnimalHospital(
    @Id
    var mgtNo: String,

    var opnsfTeamCode: String? = null,
    var apvPermYmd: String? = null,
    var trdStateGbn: String? = null,
    var trdStateNm: String? = null,
    var siteTel: String? = null,
    var siteWhlAddr: String? = null,
    var rdnWhlAddr: String? = null,
    var bplcNm: String? = null,
    var uptaeNm: String? = null,
    var xCode: String? = null,
    var yCode: String? = null,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)