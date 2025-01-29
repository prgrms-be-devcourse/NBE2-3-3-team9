package com.example.nbe233team9.domain.community.repository

import com.example.nbe233team9.domain.community.model.Community
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CommunityRepository : JpaRepository<Community, Long> {

    @Query(
        "SELECT c FROM Community c WHERE " +
                "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')))"
    )
    fun searchByKeyWord(keyword: String, pageable: Pageable): Page<Community>

    @Query("SELECT c FROM Community c WHERE c.animalSpecies = :category")
    fun searchByCategory(category: String, pageable: Pageable): Page<Community>

    @Query(
        "SELECT c FROM Community c WHERE " +
                "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
                "AND c.animalSpecies = :category"
    )
    fun searchByKeyWordAndCategory(keyword: String, category: String, pageable: Pageable): Page<Community>

    fun findByUserId(userId: Long): List<Community>

    @Query("SELECT DISTINCT c.animalSpecies FROM Community c")
    fun findDistinctAnimalSpecies(): List<String>
}