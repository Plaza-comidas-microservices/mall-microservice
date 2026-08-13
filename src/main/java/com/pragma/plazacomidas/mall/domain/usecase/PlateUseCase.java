package com.pragma.plazacomidas.mall.domain.usecase;

import com.pragma.plazacomidas.mall.domain.api.IPlateServicePort;
import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.model.PlateModel;
import com.pragma.plazacomidas.mall.domain.spi.IPlatePersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;

public class PlateUseCase implements IPlateServicePort {

    private final IPlatePersistencePort platePersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    public PlateUseCase(IPlatePersistencePort platePersistencePort, IRestaurantPersistencePort restaurantPersistencePort) {
        this.platePersistencePort = platePersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public PlateModel createPlate(PlateModel plateModel) {
        String name = plateModel.getName();
        int price = plateModel.getPrice();
        String description = plateModel.getDescription();
        String url = plateModel.getUrl();
        String category = plateModel.getCategory();
        Long restaurantId = plateModel.getRestaurantId();

        if (name == null || name.isBlank()) {
            throw new DomainException("El nombre del plato no puede estar vacío");
        } else if (price <= 0) {
            throw new DomainException("El precio del plato debe ser un número entero positivo mayor a 0");
        } else if (description == null || description.isBlank()) {
            throw new DomainException("La descripción del plato no puede estar vacía");
        } else if (url == null || url.isBlank()) {
            throw new DomainException("La URL de la imagen no puede estar vacía");
        } else if (category == null || category.isBlank()) {
            throw new DomainException("La categoría no puede estar vacía");
        } else if (restaurantId == null) {
            throw new DomainException("El plato debe estar asociado a un restaurante");
        } else if (!restaurantPersistencePort.existsById(restaurantId)) {
            throw new DomainException("El restaurante indicado no existe");
        }

        plateModel.setActive(true);
        return platePersistencePort.savePlate(plateModel);
    }
}
