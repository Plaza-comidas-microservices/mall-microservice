package com.pragma.plazacomidas.mall.domain.api;

import com.pragma.plazacomidas.mall.domain.model.EfficiencyReportModel;

public interface IEfficiencyServicePort {

    EfficiencyReportModel getEfficiencyReport(Long restaurantId, Long authenticatedOwnerId);
}
