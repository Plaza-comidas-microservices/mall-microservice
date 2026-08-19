package com.pragma.plazacomidas.mall.infrastructure.out.jpa.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazacomidas.mall.domain.model.PlateModel;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.entity.PlateEntity;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IPlateEntityMapper {
    PlateEntity toEntity(PlateModel plateModel);
    PlateModel toPlateModel(PlateEntity plateEntity);
    List<PlateModel> toPlateModelList(List<PlateEntity> plateEntityList);
}
