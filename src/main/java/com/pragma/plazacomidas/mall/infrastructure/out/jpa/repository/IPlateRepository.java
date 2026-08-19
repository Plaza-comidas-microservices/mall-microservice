package com.pragma.plazacomidas.mall.infrastructure.out.jpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pragma.plazacomidas.mall.infrastructure.out.jpa.entity.PlateEntity;

public interface IPlateRepository extends JpaRepository<PlateEntity, Long> {

    @Query("SELECT p FROM PlateEntity p WHERE p.restaurantId = :restaurantId "
            + "AND p.active = true "
            + "AND (:category IS NULL OR p.category = :category)")
    Page<PlateEntity> findByRestaurantIdAndOptionalCategory(
            @Param("restaurantId") Long restaurantId,
            @Param("category") String category,
            Pageable pageable);
}
