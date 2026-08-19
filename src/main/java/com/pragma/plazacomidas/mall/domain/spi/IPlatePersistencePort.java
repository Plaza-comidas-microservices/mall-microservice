package com.pragma.plazacomidas.mall.domain.spi;

import java.util.List;

import com.pragma.plazacomidas.mall.domain.model.PlateModel;

public interface IPlatePersistencePort {

    PlateModel savePlate(PlateModel plateModel);

    PlateModel getPlateById(Long plateId);

    List<PlateModel> getAllPlates(Long restaurantId, String category, int page, int size);
}
