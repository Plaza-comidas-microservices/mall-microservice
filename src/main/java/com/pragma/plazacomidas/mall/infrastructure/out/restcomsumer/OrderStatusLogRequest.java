package com.pragma.plazacomidas.mall.infrastructure.out.restcomsumer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderStatusLogRequest {
    private Long orderId;
    private Long clientId;
    private String previousStatus;
    private String newStatus;
}
