package com.pragma.plazacomidas.mall.domain.spi;

public interface ITraceabilityPort {

    void logStatusChange(Long orderId, Long clientId, String previousStatus, String newStatus);
}
