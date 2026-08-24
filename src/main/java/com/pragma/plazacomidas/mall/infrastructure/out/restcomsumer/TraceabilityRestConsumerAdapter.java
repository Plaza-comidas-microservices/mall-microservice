package com.pragma.plazacomidas.mall.infrastructure.out.restcomsumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.pragma.plazacomidas.mall.domain.spi.ITraceabilityPort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TraceabilityRestConsumerAdapter implements ITraceabilityPort {

    private final RestTemplate restTemplate;
    private final String traceabilityServiceUrl;

    public TraceabilityRestConsumerAdapter(RestTemplate restTemplate,
        @Value("${traceability.service.url}") String traceabilityServiceUrl) {
            this.restTemplate = restTemplate;
            this.traceabilityServiceUrl = traceabilityServiceUrl;
        }

    @Override
    public void logStatusChange(Long orderId, Long clientId, String previousStatus, String newStatus) {
        try {
            String url = traceabilityServiceUrl + "/api/v1/traceability/log";
            OrderStatusLogRequest request = new OrderStatusLogRequest(orderId, clientId, previousStatus, newStatus);
            restTemplate.postForObject(url, request, Void.class);
        } catch (RestClientException e) {
            log.warn("No se pudo registrar la trazabilidad del pedido {}: {}", orderId, e.getMessage());
        }
    }
}
