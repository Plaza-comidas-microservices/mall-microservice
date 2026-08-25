package com.pragma.plazacomidas.mall.infrastructure.out.restcomsumer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.model.OrderTimingModel;
import com.pragma.plazacomidas.mall.domain.spi.IOrderTimingPort;
import com.pragma.plazacomidas.mall.domain.spi.ITraceabilityPort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TraceabilityRestConsumerAdapter implements ITraceabilityPort, IOrderTimingPort {

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

    @Override
    public List<OrderTimingModel> getOrderTimings(List<Long> orderIds) {
        try {
            String url = traceabilityServiceUrl + "/api/v1/traceability/orders/timings";
            HttpEntity<OrderTimingsRequest> requestEntity = new HttpEntity<>(new OrderTimingsRequest(orderIds));
            List<OrderTimingResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                    new ParameterizedTypeReference<List<OrderTimingResponse>>() {}).getBody();

            if (response == null) {
                throw new DomainException("No se pudo obtener el tiempo de los pedidos");
            }

            return response.stream()
                    .map(timing -> new OrderTimingModel(timing.getOrderId(), timing.getStartedAt(), timing.getEndedAt()))
                    .collect(Collectors.toList());
        } catch (RestClientException e) {
            throw new DomainException("No se pudo consultar la trazabilidad de los pedidos, el servicio parece no estar disponible ahora");
        }
    }
}
