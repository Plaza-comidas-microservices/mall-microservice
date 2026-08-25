package com.pragma.plazacomidas.mall.application.handler.impl;

import org.springframework.stereotype.Service;

import com.pragma.plazacomidas.mall.application.dto.response.EfficiencyReportResponseDto;
import com.pragma.plazacomidas.mall.application.handler.IEfficiencyHandler;
import com.pragma.plazacomidas.mall.application.mapper.IEfficiencyReportMapper;
import com.pragma.plazacomidas.mall.domain.api.IEfficiencyServicePort;
import com.pragma.plazacomidas.mall.domain.model.EfficiencyReportModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EfficiencyHandler implements IEfficiencyHandler {

    private final IEfficiencyServicePort efficiencyServicePort;
    private final IEfficiencyReportMapper efficiencyReportMapper;

    @Override
    public EfficiencyReportResponseDto getEfficiencyReport(Long restaurantId, Long authenticatedOwnerId) {
        EfficiencyReportModel report = efficiencyServicePort.getEfficiencyReport(restaurantId, authenticatedOwnerId);
        return efficiencyReportMapper.toResponse(report);
    }
}
