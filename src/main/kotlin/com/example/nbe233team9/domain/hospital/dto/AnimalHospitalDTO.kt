package com.example.nbe233team9.domain.hospital.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.example.nbe233team9.domain.hospital.model.AnimalHospital
import lombok.*

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AnimalHospitalDTO(
    @JsonProperty("OPNSFTEAMCODE")
    var opnsfTeamCode: String? = null,

    @JsonProperty("MGTNO")
    var mgtNo: String = "",

    @JsonProperty("APVPERMYMD")
    var apvPermYmd: String? = null,

    @JsonProperty("TRDSTATEGBN")
    var trdStateGbn: String? = null,

    @JsonProperty("TRDSTATENM")
    var trdStateNm: String? = null,

    @JsonProperty("SITETEL")
    var siteTel: String? = null,

    @JsonProperty("SITEWHLADDR")
    var siteWhlAddr: String? = null,

    @JsonProperty("RDNWHLADDR")
    var rdnWhlAddr: String? = null,

    @JsonProperty("BPLCNM")
    var bplcNm: String? = null,

    @JsonProperty("UPTAENM")
    var uptaeNm: String? = null,

    @JsonProperty("X")
    var xCode: String? = null,

    @JsonProperty("Y")
    var yCode: String? = null,

    @JsonProperty("latitude")
    var latitude: Double = 0.0,

    @JsonProperty("longitude")
    var longitude: Double = 0.0
) {
    fun toEntity(): AnimalHospital {
        return AnimalHospital(
            opnsfTeamCode = this.opnsfTeamCode,
            mgtNo = this.mgtNo,
            apvPermYmd = this.apvPermYmd,
            trdStateGbn = this.trdStateGbn,
            trdStateNm = this.trdStateNm,
            siteTel = this.siteTel,
            siteWhlAddr = this.siteWhlAddr,
            rdnWhlAddr = this.rdnWhlAddr,
            bplcNm = this.bplcNm,
            uptaeNm = this.uptaeNm,
            xCode = this.xCode,
            yCode = this.yCode,
            latitude = this.latitude,
            longitude = this.longitude
        )
    }
}