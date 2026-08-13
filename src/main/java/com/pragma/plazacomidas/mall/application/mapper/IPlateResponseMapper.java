package com.pragma.plazacomidas.mall.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazacomidas.mall.application.dto.response.PlateResponseDto;
import com.pragma.plazacomidas.mall.domain.model.PlateModel;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IPlateResponseMapper {
    PlateResponseDto toResponse(PlateModel plateModel);
}
