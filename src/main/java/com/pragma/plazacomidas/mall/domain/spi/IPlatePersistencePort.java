package com.pragma.plazacomidas.mall.domain.spi;

import com.pragma.plazacomidas.mall.domain.model.PlateModel;

public interface IPlatePersistencePort {

    PlateModel savePlate(PlateModel plateModel);

    PlateModel getPlateById(Long plateId);
}
