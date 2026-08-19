package com.pragma.plazacomidas.mall.application.handler;

import com.pragma.plazacomidas.mall.application.dto.request.PlateRequestDto;
import com.pragma.plazacomidas.mall.application.dto.request.UpdatePlateRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.PlateResponseDto;
import com.pragma.plazacomidas.mall.application.dto.request.UpdatePlateStatusRequestDto;


public interface IPlateHandler {
    PlateResponseDto savePlate(PlateRequestDto plateRequestDto, Long authenticatedUserId);

    PlateResponseDto updatePlate(Long plateId, UpdatePlateRequestDto updatePlateRequestDto, Long authenticatedUserId);

    PlateResponseDto updateStatusPlate(Long plateId, UpdatePlateStatusRequestDto updatePlateStatusRequestDto, Long authenticatedUserId);
}
