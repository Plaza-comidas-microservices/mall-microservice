package com.pragma.plazacomidas.mall.infrastructure.out.restcomsumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.spi.IClientContactPort;
import com.pragma.plazacomidas.mall.domain.spi.IUserValidationPort;

//Al hacerlo con component no es necesario registrar el bean en el beanconfig ya que el Spring lo detecta solo
@Component
public class UserRestConsumerAdapter implements IUserValidationPort, IClientContactPort {

    private static final String ROLE_OWNER = "ROLE_OWNER";

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserRestConsumerAdapter(RestTemplate restTemplate, 
        @Value("${users.service.url}") String userServiceUrl){
            this.restTemplate = restTemplate;
            this.userServiceUrl = userServiceUrl;
        }

    @Override
    public boolean isOwner(Long ownerId) {
        boolean isOwner = false;
        try {
            String url = userServiceUrl + "/api/v1/owner/" +  ownerId;
            UserValidationResponse response = restTemplate.getForObject(url, UserValidationResponse.class);
            if(response != null && ROLE_OWNER.equals(response.getRole())){
                isOwner = true;
            }
            return isOwner;
           
        } catch (HttpClientErrorException.NotFound e) {
            return isOwner;
        }catch(ResourceAccessException e){
            throw new DomainException("No se pudo validar el propietario, el servicio parece no estar dispoible ahora");
        }
    }

    @Override
    public String getClientPhone(Long clientId) {
        try {
            String url = userServiceUrl + "/api/v1/client/" + clientId;
            ClientContactResponse response = restTemplate.getForObject(url, ClientContactResponse.class);
            if (response == null) {
                throw new DomainException("No se pudo obtener el teléfono del cliente");
            }
            return response.getPhone();
        } catch (HttpClientErrorException.NotFound e) {
            throw new DomainException("El cliente no existe");
        } catch (ResourceAccessException e) {
            throw new DomainException("No se pudo obtener el teléfono del cliente, el servicio parece no estar disponible ahora");
        }
    }

}