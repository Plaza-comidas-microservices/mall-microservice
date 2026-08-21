package com.pragma.plazacomidas.mall.infrastructure.out.restcomsumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.spi.INotificationPort;

@Component
public class NotificationRestConsumerAdapter implements INotificationPort {

    private final RestTemplate restTemplate;
    private final String messagingServiceUrl;

    public NotificationRestConsumerAdapter(RestTemplate restTemplate,
        @Value("${messaging.service.url}") String messagingServiceUrl) {
            this.restTemplate = restTemplate;
            this.messagingServiceUrl = messagingServiceUrl;
        }

    @Override
    public void sendOrderReadySms(String phone, String securityPin) {
        String url = messagingServiceUrl + "/api/v1/notification/sms";
        String msj = "Tu pedido está listo. Tu pin de seguridad para reclamarlo es: " + securityPin;
        try {
            restTemplate.postForObject(url, new SmsNotificationRequest(phone, msj), Void.class);
        } catch (HttpStatusCodeException e) {
            throw new DomainException("No se pudo notificar al cliente que su pedido está listo");
        } catch (ResourceAccessException e) {
            throw new DomainException("El servicio de mensajería no está disponible ahora");
        }
    }

}
