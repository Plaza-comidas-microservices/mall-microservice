package com.pragma.plazacomidas.mall.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEfficiencyModel {
    private Long employeeId;
    private double averageDurationSeconds;
}
