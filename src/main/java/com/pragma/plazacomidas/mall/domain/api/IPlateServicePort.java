package com.pragma.plazacomidas.mall.domain.api;

import java.util.List;

import com.pragma.plazacomidas.mall.domain.model.PlateModel;

public interface IPlateServicePort {
    PlateModel createPlate(PlateModel plateModel, Long authenticatedUserId);

    PlateModel updatePlate(Long plateId, int newPrice, String newDescription, Long authenticatedUserId);

    PlateModel toggPlateStatus(Long plateId, boolean active, Long authenticatedUserId);

    List<PlateModel> getAllPlates(Long restaurantId, String category, int page, int size);
}
