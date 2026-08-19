package com.pragma.plazacomidas.mall.infrastructure.input.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pragma.plazacomidas.mall.application.dto.request.RestaurantRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.RestaurantResponseDto;
import com.pragma.plazacomidas.mall.application.dto.response.RestaurantValidationResponseDto;
import com.pragma.plazacomidas.mall.application.handler.IRestaurantHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/restaurant")
@RequiredArgsConstructor
public class RestaurantRestController {

    private final IRestaurantHandler restaurantHandler;

    @Operation(summary = "Create a new restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid restaurant data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Missing, invalid or insufficient token: only an ADMIN can create a restaurant", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<RestaurantResponseDto> createRestaurant(@RequestBody RestaurantRequestDto restaurantRequestDto) {
        RestaurantResponseDto restaurantResponseDto = restaurantHandler.saveRestaurant(restaurantRequestDto);
        return new ResponseEntity<>(restaurantResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Get restaurant by id (used for validation between microservices)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestaurantValidationResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Restaurant not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantValidationResponseDto> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantHandler.getRestaurantById(id));
    }
}
