package com.pragma.plazacomidas.mall.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EfficiencyReportModel {
    private List<OrderEfficiencyModel> orderEfficiencies;
    private List<EmployeeEfficiencyModel> employeeRanking;
}
