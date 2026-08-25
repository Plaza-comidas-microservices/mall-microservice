package com.pragma.plazacomidas.mall.application.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EfficiencyReportResponseDto {
    private List<OrderEfficiencyResponseDto> orderEfficiencies;
    private List<EmployeeEfficiencyResponseDto> employeeRanking;
}
