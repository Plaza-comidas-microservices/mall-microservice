package com.pragma.plazacomidas.mall.infrastructure.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pragma.plazacomidas.mall.application.dto.request.PlateRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.PlateResponseDto;
import com.pragma.plazacomidas.mall.application.handler.IPlateHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/plate")
@RequiredArgsConstructor
public class PlateRestController {

    private final IPlateHandler plateHandler;

    @Operation(summary = "Create a new plate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plate created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlateResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid plate data", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<PlateResponseDto> createPlate(@RequestBody PlateRequestDto plateRequestDto) {
        PlateResponseDto plateResponseDto = plateHandler.savePlate(plateRequestDto);
        return new ResponseEntity<>(plateResponseDto, HttpStatus.CREATED);
    }
}
