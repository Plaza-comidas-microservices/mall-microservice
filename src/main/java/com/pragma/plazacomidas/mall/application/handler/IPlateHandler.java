package com.pragma.plazacomidas.mall.application.handler;

import com.pragma.plazacomidas.mall.application.dto.request.PlateRequestDto;
import com.pragma.plazacomidas.mall.application.dto.request.UpdatePlateRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.PlateResponseDto;

public interface IPlateHandler {
    PlateResponseDto savePlate(PlateRequestDto plateRequestDto);

    PlateResponseDto updatePlate(Long plateId, UpdatePlateRequestDto updatePlateRequestDto);
}
