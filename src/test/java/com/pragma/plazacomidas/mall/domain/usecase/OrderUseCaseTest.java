package com.pragma.plazacomidas.mall.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.model.OrderItemModel;
import com.pragma.plazacomidas.mall.domain.model.OrderModel;
import com.pragma.plazacomidas.mall.domain.model.PlateModel;
import com.pragma.plazacomidas.mall.domain.spi.IOrderPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IPlatePersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IPlatePersistencePort platePersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    private OrderUseCase orderUseCase;

    private static final Long CLIENT_ID = 1L;
    private static final Long RESTAURANT_ID = 1L;

    @BeforeEach
    void setUp() {
        orderUseCase = new OrderUseCase(orderPersistencePort, platePersistencePort, restaurantPersistencePort);
    }

    private OrderModel buildValidOrder() {
        OrderItemModel item = new OrderItemModel(1L, 2);
        return new OrderModel(null, null, RESTAURANT_ID, null, List.of(item));
    }

    private PlateModel buildActivePlate(Long restaurantId) {
        PlateModel plate = new PlateModel();
        plate.setId(1L);
        plate.setRestaurantId(restaurantId);
        plate.setActive(true);
        return plate;
    }

    // ---------- HAPPY PATH ----------

    @Test
    void shouldCreateOrderSuccessfullyWhenDataIsValid() {
        OrderModel orderModel = buildValidOrder();
        OrderModel savedOrder = new OrderModel(1L, CLIENT_ID, RESTAURANT_ID, "PENDIENTE", orderModel.getItems());

        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(orderPersistencePort.hasActiveOrder(CLIENT_ID)).thenReturn(false);
        when(platePersistencePort.getPlateById(1L)).thenReturn(buildActivePlate(RESTAURANT_ID));
        when(orderPersistencePort.saveOrder(any(OrderModel.class))).thenReturn(savedOrder);

        OrderModel result = orderUseCase.createOrder(orderModel, CLIENT_ID);

        assertEquals(1L, result.getId());
        assertEquals("PENDIENTE", result.getStatus());
        verify(orderPersistencePort, times(1)).saveOrder(any(OrderModel.class));
    }

    // ---------- SAD PATHS ----------

    @Test
    void shouldThrowExceptionWhenRestaurantIdIsNull() {
        OrderModel orderModel = buildValidOrder();
        orderModel.setRestaurantId(null);

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.createOrder(orderModel, CLIENT_ID));

        assertEquals("El restaurante no existe", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenRestaurantDoesNotExist() {
        OrderModel orderModel = buildValidOrder();
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(false);

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.createOrder(orderModel, CLIENT_ID));

        assertEquals("El restaurante no existe", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenItemsListIsNull() {
        OrderModel orderModel = buildValidOrder();
        orderModel.setItems(null);
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.createOrder(orderModel, CLIENT_ID));

        assertEquals("No hay pedidos", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenClientAlreadyHasAnActiveOrder() {
        OrderModel orderModel = buildValidOrder();
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(orderPersistencePort.hasActiveOrder(CLIENT_ID)).thenReturn(true);

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.createOrder(orderModel, CLIENT_ID));

        assertEquals("Ya tienes un pedido en curso, no puedes realizar otro", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenItemQuantityIsZeroOrNegative() {
        OrderModel orderModel = new OrderModel(null, null, RESTAURANT_ID, null, List.of(new OrderItemModel(1L, 0)));
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(orderPersistencePort.hasActiveOrder(CLIENT_ID)).thenReturn(false);
        when(platePersistencePort.getPlateById(1L)).thenReturn(buildActivePlate(RESTAURANT_ID));

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.createOrder(orderModel, CLIENT_ID));

        assertEquals("No hay unidades disponibles", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenPlateBelongsToAnotherRestaurant() {
        OrderModel orderModel = buildValidOrder();
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(orderPersistencePort.hasActiveOrder(CLIENT_ID)).thenReturn(false);
        when(platePersistencePort.getPlateById(1L)).thenReturn(buildActivePlate(999L));

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.createOrder(orderModel, CLIENT_ID));

        assertEquals("El plato no pertenece a este restaurante", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenPlateIsNotActive() {
        OrderModel orderModel = buildValidOrder();
        PlateModel inactivePlate = buildActivePlate(RESTAURANT_ID);
        inactivePlate.setActive(false);

        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(orderPersistencePort.hasActiveOrder(CLIENT_ID)).thenReturn(false);
        when(platePersistencePort.getPlateById(1L)).thenReturn(inactivePlate);

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.createOrder(orderModel, CLIENT_ID));

        assertEquals("Este plato está actualmente fuera del menú", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }
}
