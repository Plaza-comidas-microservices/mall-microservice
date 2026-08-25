package com.pragma.plazacomidas.mall.application.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeEfficiencyResponseDto {
    private Long employeeId;
    private double averageDurationSeconds;
}
