package com.pragma.plazacomidas.mall.application.handler.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pragma.plazacomidas.mall.application.dto.request.PlateRequestDto;
import com.pragma.plazacomidas.mall.application.dto.request.UpdatePlateRequestDto;
import com.pragma.plazacomidas.mall.application.dto.request.UpdatePlateStatusRequestDto;
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
    public PlateResponseDto savePlate(PlateRequestDto plateRequestDto, Long authenticatedUserId) {
        PlateModel plateModel = plateRequestMapper.toPlate(plateRequestDto);
        PlateModel createdPlate = plateServicePort.createPlate(plateModel, authenticatedUserId);
        return plateResponseMapper.toResponse(createdPlate);
    }

    @Override
    public PlateResponseDto updatePlate(Long plateId, UpdatePlateRequestDto updatePlateRequestDto, Long authenticatedUserId) {
        PlateModel plateUpdated = plateServicePort.updatePlate(plateId, updatePlateRequestDto.getPrice(), updatePlateRequestDto.getDescription(), authenticatedUserId);
        return plateResponseMapper.toResponse(plateUpdated);
    }

    @Override
    public PlateResponseDto updateStatusPlate(Long plateId, UpdatePlateStatusRequestDto updatePlateStatusRequestDto, Long authenticatedUserId) {
        PlateModel plateUpdatedStatus = plateServicePort.toggPlateStatus(plateId, updatePlateStatusRequestDto.isActive(), authenticatedUserId);
        return plateResponseMapper.toResponse(plateUpdatedStatus);
    }
}
