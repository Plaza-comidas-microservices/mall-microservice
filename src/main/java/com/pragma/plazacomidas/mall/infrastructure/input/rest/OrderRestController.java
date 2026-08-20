package com.pragma.plazacomidas.mall.infrastructure.input.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pragma.plazacomidas.mall.application.dto.request.OrderRequestDto;
import com.pragma.plazacomidas.mall.application.dto.response.OrderListResponseDto;
import com.pragma.plazacomidas.mall.application.dto.response.OrderResponseDto;
import com.pragma.plazacomidas.mall.application.handler.IOrderHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderRestController {

    private final IOrderHandler orderHandler;

    @Operation(summary = "Place an order for a restaurant's plates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid order data", content = @Content),
            @ApiResponse(responseCode = "403", description = "Missing, invalid or insufficient token: only a CLIENT can place an order", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        Long authenticatedClientId = (Long) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        OrderResponseDto orderResponseDto = orderHandler.saveOrder(orderRequestDto, authenticatedClientId);
        return new ResponseEntity<>(orderResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "List a restaurant's orders filtered by status, paginated (employee-only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = OrderListResponseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid status or pagination parameters", content = @Content),
            @ApiResponse(responseCode = "403", description = "Missing, invalid or insufficient token: only an EMPLOYEE can list their restaurant's orders", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<List<OrderListResponseDto>> getOrders(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long employeeRestaurantId = (Long) SecurityContextHolder.getContext().getAuthentication().getDetails();
        return ResponseEntity.ok(orderHandler.getOrdersByRestaurantAndStatus(employeeRestaurantId, status, page, size));
    }
}
