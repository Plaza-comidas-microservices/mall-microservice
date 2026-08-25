package com.pragma.plazacomidas.mall.infrastructure.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pragma.plazacomidas.mall.application.dto.response.EfficiencyReportResponseDto;
import com.pragma.plazacomidas.mall.application.handler.IEfficiencyHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class EfficiencyRestController {

    private final IEfficiencyHandler efficiencyHandler;

    @Operation(summary = "Get the delivery time per order and the average time ranking per employee, for one of the owner's restaurants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Efficiency report returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = EfficiencyReportResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Restaurant does not exist or does not belong to you", content = @Content),
            @ApiResponse(responseCode = "403", description = "Missing, invalid or insufficient token: only an OWNER can query this report", content = @Content)
    })
    @GetMapping("/efficiency")
    public ResponseEntity<EfficiencyReportResponseDto> getEfficiencyReport(@RequestParam Long restaurantId) {
        Long authenticatedOwnerId = (Long) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        return ResponseEntity.ok(efficiencyHandler.getEfficiencyReport(restaurantId, authenticatedOwnerId));
    }
}
