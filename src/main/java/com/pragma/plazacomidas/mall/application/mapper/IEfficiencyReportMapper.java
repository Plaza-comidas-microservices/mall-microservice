package com.pragma.plazacomidas.mall.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.pragma.plazacomidas.mall.application.dto.response.EfficiencyReportResponseDto;
import com.pragma.plazacomidas.mall.domain.model.EfficiencyReportModel;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface IEfficiencyReportMapper {

    EfficiencyReportResponseDto toResponse(EfficiencyReportModel efficiencyReportModel);
}
