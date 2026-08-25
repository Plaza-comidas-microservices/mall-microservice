package com.pragma.plazacomidas.mall.infrastructure.out.restcomsumer;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderTimingResponse {
    private Long orderId;
    private Instant startedAt;
    private Instant endedAt;
}
