package com.pragma.plazacomidas.mall.infrastructure.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pragma.plazacomidas.mall.application.dto.request.PlateRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.PlateResponseDto;
import com.pragma.plazacomidas.mall.application.handler.IPlateHandler;
import com.pragma.plazacomidas.mall.application.dto.request.UpdatePlateRequestDto;

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

    @Operation(summary = "Update the price and description of an existing plate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plate updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PlateResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid plate data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Plate not found", content = @Content)
    })
    @PatchMapping("/{id}")
    public ResponseEntity<PlateResponseDto> updatePlate(@PathVariable Long id, @RequestBody UpdatePlateRequestDto updatePlateRequestDto) {
        PlateResponseDto plateResponseDto = plateHandler.updatePlate(id, updatePlateRequestDto);
        return new ResponseEntity<>(plateResponseDto, HttpStatus.OK);
    }
}
