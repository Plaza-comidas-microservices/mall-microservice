package com.pragma.plazacomidas.mall.application.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazacomidas.mall.application.dto.response.PlateMenuResponseDto;
import com.pragma.plazacomidas.mall.domain.model.PlateModel;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IPlateMenuMapper {
    PlateMenuResponseDto toResponse(PlateModel plateModel);
    List<PlateMenuResponseDto> toResponseList(List<PlateModel> plateModelList);
}
