package com.pragma.plazacomidas.mall.domain.api;

import com.pragma.plazacomidas.mall.domain.model.PlateModel;

public interface IPlateServicePort {
    PlateModel createPlate(PlateModel plateModel, Long authenticatedUserId);

    PlateModel updatePlate(Long plateId, int newPrice, String newDescription, Long authenticatedUserId);
}
