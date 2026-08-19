package com.pragma.plazacomidas.mall.domain.usecase;

import java.util.List;

import com.pragma.plazacomidas.mall.domain.api.IRestaurantServicePort;
import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IUserValidationPort;

public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserValidationPort userValidationPort;

    public RestaurantUseCase(IRestaurantPersistencePort restaurantPersistencePort, IUserValidationPort userValidationPort) {
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.userValidationPort = userValidationPort;
    }

    @Override
    public RestaurantModel createRestaurant(RestaurantModel restaurantModel) {
        String name = restaurantModel.getName();
        String nit = restaurantModel.getNit();
        String phone = restaurantModel.getPhone();
        String address = restaurantModel.getAddress();
        String logoUrl = restaurantModel.getLogoUrl();
        Long ownerId = restaurantModel.getOwnerId();

        if (name == null || name.isBlank() || name.matches("\\d+")) {
            throw new DomainException("El nombre del restaurante es obligatorio y no puede contener solo números");
        } else if (nit == null || !nit.matches("\\d+")) {
            throw new DomainException("El NIT debe ser únicamente numérico");
        } else if (phone == null || !phone.matches("^\\+?\\d{1,13}$")) {
            throw new DomainException("El teléfono no es válido. Ejemplo +573005698325");
        } else if (address == null || address.isBlank()) {
            throw new DomainException("La dirección es obligatoria");
        } else if (logoUrl == null || logoUrl.isBlank()) {
            throw new DomainException("La URL del logo es obligatoria");
        } else if (restaurantModel.getOwnerId() == null) {
            throw new DomainException("El id del propietario es obligatorio");
        }else if (!userValidationPort.isOwner(ownerId)){
            throw new DomainException("El id del propietario no es válido");
        }


        return restaurantPersistencePort.saveRestaurant(restaurantModel);
    }

    @Override
    public RestaurantModel getRestaurantById(Long restaurantId) {
        return restaurantPersistencePort.getRestaurantById(restaurantId);
    }

    @Override
    public List<RestaurantModel> getAllRestaurants(int page, int size) {
        if (page < 0) {
            throw new DomainException("El número de página no puede ser negativo");
        } else if (size <= 0) {
            throw new DomainException("El tamaño de página debe ser mayor a 0");
        }
        return restaurantPersistencePort.getAllRestaurants(page, size);
    }
}
