package com.pragma.plazacomidas.mall.application.handler.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pragma.plazacomidas.mall.application.dto.request.PlateRequestDto;
import com.pragma.plazacomidas.mall.application.dto.request.UpdatePlateRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.PlateResponseDto;
import com.pragma.plazacomidas.mall.application.handler.IPlateHandler;
import com.pragma.plazacomidas.mall.application.mapper.IPlateRequestMapper;
import com.pragma.plazacomidas.mall.application.mapper.IPlateResponseMapper;
import com.pragma.plazacomidas.mall.domain.api.IPlateServicePort;
import com.pragma.plazacomidas.mall.domain.model.PlateModel;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional
@Service
public class PlateHandler implements IPlateHandler {

    private final IPlateServicePort plateServicePort;
    private final IPlateRequestMapper plateRequestMapper;
    private final IPlateResponseMapper plateResponseMapper;

    @Override
    public PlateResponseDto savePlate(PlateRequestDto plateRequestDto) {
        PlateModel plateModel = plateRequestMapper.toPlate(plateRequestDto);
        PlateModel createdPlate = plateServicePort.createPlate(plateModel);
        return plateResponseMapper.toResponse(createdPlate);
    }

    @Override
    public PlateResponseDto updatePlate(Long plateId, UpdatePlateRequestDto updatePlateRequestDto) {
        PlateModel plateUpdated = plateServicePort.updatePlate(
                plateId, updatePlateRequestDto.getPrice(), updatePlateRequestDto.getDescription());
        return plateResponseMapper.toResponse(plateUpdated);
    }
}
