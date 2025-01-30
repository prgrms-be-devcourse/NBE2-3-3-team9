package com.example.nbe233team9.domain.hospital.service

import org.locationtech.proj4j.*
import org.springframework.stereotype.Component

@Component
class CoordinateConverter {

    private val crsFactory = CRSFactory()
    private val srcCrs: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:5179") // UTM-K
    private val destCrs: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326") // WGS84
    private val transform: CoordinateTransform = CoordinateTransformFactory().createTransform(srcCrs, destCrs)

    fun convertEPSG5179ToWGS84(x: Double, y: Double): DoubleArray {
        val sourceCoord = ProjCoordinate(x, y)
        val targetCoord = ProjCoordinate()
        transform.transform(sourceCoord, targetCoord)
        return doubleArrayOf(targetCoord.y, targetCoord.x) // [위도, 경도]
    }
}
