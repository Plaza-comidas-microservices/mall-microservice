package com.pragma.plazacomidas.mall.application.handler;

import com.pragma.plazacomidas.mall.application.dto.response.EfficiencyReportResponseDto;

public interface IEfficiencyHandler {

    EfficiencyReportResponseDto getEfficiencyReport(Long restaurantId, Long authenticatedOwnerId);
}
