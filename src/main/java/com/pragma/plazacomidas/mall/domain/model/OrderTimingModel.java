package com.pragma.plazacomidas.mall.domain.model;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimingModel {
    private Long orderId;
    private Instant startedAt;
    private Instant endedAt;
}
