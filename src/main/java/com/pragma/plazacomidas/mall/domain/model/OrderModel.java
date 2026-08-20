package com.pragma.plazacomidas.mall.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderModel {
    private Long id;
    private Long clientId;
    private Long restaurantId;
    private String status;
    private Long assignedEmployeeId;
    private List<OrderItemModel> items;
}
