package com.pragma.plazacomidas.mall.infrastructure.out.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.pragma.plazacomidas.mall.infrastructure.out.jpa.entity.OrderEntity;

public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {

    boolean existsByClientIdAndStatusIn(Long clientId, List<String> status);

    Page<OrderEntity> findByRestaurantIdAndStatus(Long restaurantId, String status, Pageable pageable);
}
