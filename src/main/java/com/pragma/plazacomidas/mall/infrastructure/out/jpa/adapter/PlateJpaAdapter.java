package com.pragma.plazacomidas.mall.infrastructure.out.jpa.adapter;

import com.pragma.plazacomidas.mall.domain.model.PlateModel;
import com.pragma.plazacomidas.mall.domain.spi.IPlatePersistencePort;
import com.pragma.plazacomidas.mall.infrastructure.exception.NoDataFoundException;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.entity.PlateEntity;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.mapper.IPlateEntityMapper;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.repository.IPlateRepository;

public class PlateJpaAdapter implements IPlatePersistencePort {

    private final IPlateRepository plateRepository;
    private final IPlateEntityMapper plateEntityMapper;

    public PlateJpaAdapter(IPlateRepository plateRepository, IPlateEntityMapper plateEntityMapper) {
        this.plateRepository = plateRepository;
        this.plateEntityMapper = plateEntityMapper;
    }

    @Override
    public PlateModel savePlate(PlateModel plateModel) {
        return plateEntityMapper.toPlateModel(
                plateRepository.save(plateEntityMapper.toEntity(plateModel)));
    }

    @Override
    public PlateModel getPlateById(Long plateId) {
        PlateEntity plateEntity = plateRepository.findById(plateId)
                .orElseThrow(NoDataFoundException::new);
        return plateEntityMapper.toPlateModel(plateEntity);
    }
}
