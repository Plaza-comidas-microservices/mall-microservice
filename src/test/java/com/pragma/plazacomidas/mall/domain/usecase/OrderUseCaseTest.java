package com.pragma.plazacomidas.mall.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
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
import com.pragma.plazacomidas.mall.domain.spi.IClientContactPort;
import com.pragma.plazacomidas.mall.domain.spi.INotificationPort;
import com.pragma.plazacomidas.mall.domain.spi.IOrderPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IPlatePersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.ITraceabilityPort;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IPlatePersistencePort platePersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IClientContactPort clientContactPort;

    @Mock
    private INotificationPort notificationPort;

    @Mock
    private ITraceabilityPort traceabilityPort;

    private OrderUseCase orderUseCase;

    private static final Long CLIENT_ID = 1L;
    private static final Long RESTAURANT_ID = 1L;

    @BeforeEach
    void setUp() {
        orderUseCase = new OrderUseCase(orderPersistencePort, platePersistencePort, restaurantPersistencePort,
            clientContactPort, notificationPort, traceabilityPort);
    }

    private OrderModel buildValidOrder() {
        OrderItemModel item = new OrderItemModel(1L, 2);
        return new OrderModel(null, null, RESTAURANT_ID, null, null, null, List.of(item));
    }

    private PlateModel buildActivePlate(Long restaurantId) {
        PlateModel plate = new PlateModel();
        plate.setId(1L);
        plate.setRestaurantId(restaurantId);
        plate.setActive(true);
        return plate;
    }

     private OrderModel buildReadyOrder(Long restaurantId, String securityPin) {
        OrderModel order = buildOrderInPreparation(restaurantId);
        order.setStatus("LISTO");
        order.setSecurityPin(securityPin);
        return order;
    }

    // ---------- HAPPY PATH ----------

    @Test
    void shouldCreateOrderSuccessfullyWhenDataIsValid() {
        OrderModel orderModel = buildValidOrder();
        OrderModel savedOrder = new OrderModel(1L, CLIENT_ID, RESTAURANT_ID, "PENDIENTE", null, null, orderModel.getItems());

        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(orderPersistencePort.hasActiveOrder(CLIENT_ID)).thenReturn(false);
        when(platePersistencePort.getPlateById(1L)).thenReturn(buildActivePlate(RESTAURANT_ID));
        when(orderPersistencePort.saveOrder(any(OrderModel.class))).thenReturn(savedOrder);

        OrderModel result = orderUseCase.createOrder(orderModel, CLIENT_ID);

        assertEquals(1L, result.getId());
        assertEquals("PENDIENTE", result.getStatus());
        verify(orderPersistencePort, times(1)).saveOrder(any(OrderModel.class));
        verify(traceabilityPort, times(1)).logStatusChange(1L, CLIENT_ID, null, "PENDIENTE");
    }

    @Test
    void shouldAssignOrderSuccessfullyWhenPendingAndSameRestaurant() {
        Long employeeId = 5L;
        when(orderPersistencePort.getOrderById(1L)).thenReturn(buildPendingOrder(RESTAURANT_ID));
        when(orderPersistencePort.saveOrder(any(OrderModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderModel result = orderUseCase.assignOrder(1L, employeeId, RESTAURANT_ID);

        assertEquals(employeeId, result.getAssignedEmployeeId());
        assertEquals("EN_PREPARACION", result.getStatus());
        verify(orderPersistencePort, times(1)).saveOrder(any(OrderModel.class));
        verify(traceabilityPort, times(1)).logStatusChange(1L, null, "PENDIENTE", "EN_PREPARACION");
    }

    @Test
    void shouldDeliverOrderSuccessfullyWhenPinIsCorrect() {
        when(orderPersistencePort.getOrderById(1L)).thenReturn(buildReadyOrder(RESTAURANT_ID, "123456"));
        when(orderPersistencePort.saveOrder(any(OrderModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderModel result = orderUseCase.deliverOrder(1L, RESTAURANT_ID, "123456");

        assertEquals("ENTREGADO", result.getStatus());
        verify(orderPersistencePort, times(1)).saveOrder(any(OrderModel.class));
        verify(traceabilityPort, times(1)).logStatusChange(1L, CLIENT_ID, "LISTO", "ENTREGADO");
    }
    
    @Test
    void shouldCancelOrderSuccessfullyWhenPendingAndOwnedByClient() {
        OrderModel order = buildPendingOrder(RESTAURANT_ID);
        order.setClientId(CLIENT_ID);
        when(orderPersistencePort.getOrderById(1L)).thenReturn(order);
        when(orderPersistencePort.saveOrder(any(OrderModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderModel result = orderUseCase.cancelOrder(1L, CLIENT_ID);

        assertEquals("CANCELADO", result.getStatus());
        verify(orderPersistencePort, times(1)).saveOrder(any(OrderModel.class));
        verify(traceabilityPort, times(1)).logStatusChange(1L, CLIENT_ID, "PENDIENTE", "CANCELADO");
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
        OrderModel orderModel = new OrderModel(null, null, RESTAURANT_ID, null, null, null, List.of(new OrderItemModel(1L, 0)));
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

    @Test
    void shouldReturnOrdersSuccessfullyWhenFilterIsValid() {
        OrderModel order = buildValidOrder();
        when(orderPersistencePort.getOrdersByRestaurantAndStatus(RESTAURANT_ID, "PENDIENTE", 0, 10))
                .thenReturn(List.of(order));

        List<OrderModel> result = orderUseCase.getOrdersByRestaurantAndStatus(RESTAURANT_ID, "PENDIENTE", 0, 10);

        assertEquals(1, result.size());
        verify(orderPersistencePort, times(1)).getOrdersByRestaurantAndStatus(RESTAURANT_ID, "PENDIENTE", 0, 10);
    }

    @Test
    void shouldThrowExceptionWhenListingOrdersWithoutEmployeeRestaurant() {
        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.getOrdersByRestaurantAndStatus(null, "PENDIENTE", 0, 10));

        assertEquals("No perteneces a ningún restaurante", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenListingOrdersWithoutStatus() {
        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.getOrdersByRestaurantAndStatus(RESTAURANT_ID, "", 0, 10));

        assertEquals("Debes indicar el estado por el cual filtrar", exception.getMessage());
    }

    private OrderModel buildPendingOrder(Long restaurantId) {
        OrderModel order = buildValidOrder();
        order.setId(1L);
        order.setRestaurantId(restaurantId);
        order.setStatus("PENDIENTE");
        return order;
    }

    @Test
    void shouldThrowExceptionWhenAssigningOrderFromAnotherRestaurant() {
        when(orderPersistencePort.getOrderById(1L)).thenReturn(buildPendingOrder(999L));

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.assignOrder(1L, 5L, RESTAURANT_ID));

        assertEquals("Este pedido no pertenece a tu restaurante", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenAssigningOrderNotPendiente() {
        OrderModel order = buildPendingOrder(RESTAURANT_ID);
        order.setStatus("EN_PREPARACION");
        when(orderPersistencePort.getOrderById(1L)).thenReturn(order);

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.assignOrder(1L, 5L, RESTAURANT_ID));

        assertEquals("Solo puedes asignarte pedidos que estén en estado PENDIENTE", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    private OrderModel buildOrderInPreparation(Long restaurantId) {
        OrderModel order = buildPendingOrder(restaurantId);
        order.setClientId(CLIENT_ID);
        order.setStatus("EN_PREPARACION");
        order.setAssignedEmployeeId(5L);
        return order;
    }

    @Test
    void shouldMarkOrderAsReadySuccessfullyWhenInPreparationAndSameRestaurant() {
        Long employeeId = 5L;
        when(orderPersistencePort.getOrderById(1L)).thenReturn(buildOrderInPreparation(RESTAURANT_ID));
        when(clientContactPort.getClientPhone(CLIENT_ID)).thenReturn("+573001234567");
        when(orderPersistencePort.saveOrder(any(OrderModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderModel result = orderUseCase.markOrderAsReady(1L, employeeId, RESTAURANT_ID);

        assertEquals("LISTO", result.getStatus());
        assertEquals(6, result.getSecurityPin().length());
        verify(notificationPort, times(1)).sendOrderReadySms(eq("+573001234567"), any());
        verify(orderPersistencePort, times(1)).saveOrder(any(OrderModel.class));
        verify(traceabilityPort, times(1)).logStatusChange(1L, CLIENT_ID, "EN_PREPARACION", "LISTO");
    }

    @Test
    void shouldThrowExceptionWhenMarkingReadyFromAnotherRestaurant() {
        when(orderPersistencePort.getOrderById(1L)).thenReturn(buildOrderInPreparation(999L));

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.markOrderAsReady(1L, 5L, RESTAURANT_ID));

        assertEquals("Este pedido no pertenece a tu restaurante", exception.getMessage());
        verify(notificationPort, never()).sendOrderReadySms(any(), any());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenMarkingReadyOrderNotInPreparation() {
        OrderModel order = buildOrderInPreparation(RESTAURANT_ID);
        order.setStatus("PENDIENTE");
        when(orderPersistencePort.getOrderById(1L)).thenReturn(order);

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.markOrderAsReady(1L, 5L, RESTAURANT_ID));

        assertEquals("Solo puedes marcar como listo un pedido que esté en preparación", exception.getMessage());
        verify(notificationPort, never()).sendOrderReadySms(any(), any());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldNotMarkOrderAsReadyWhenNotificationFails() {
        when(orderPersistencePort.getOrderById(1L)).thenReturn(buildOrderInPreparation(RESTAURANT_ID));
        when(clientContactPort.getClientPhone(CLIENT_ID)).thenReturn("+573001234567");
        doThrow(new DomainException("El servicio de mensajería no está disponible ahora"))
                .when(notificationPort).sendOrderReadySms(any(), any());

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.markOrderAsReady(1L, 5L, RESTAURANT_ID));

        assertEquals("El servicio de mensajería no está disponible ahora", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenDeliveringOrderFromAnotherRestaurant() {
        when(orderPersistencePort.getOrderById(1L)).thenReturn(buildReadyOrder(999L, "123456"));

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.deliverOrder(1L, RESTAURANT_ID, "123456"));

        assertEquals("Este pedido no pertenece a tu restaurante", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenDeliveringOrderNotReady() {
        OrderModel order = buildReadyOrder(RESTAURANT_ID, "123456");
        order.setStatus("EN_PREPARACION");
        when(orderPersistencePort.getOrderById(1L)).thenReturn(order);

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.deliverOrder(1L, RESTAURANT_ID, "123456"));

        assertEquals("Solo puedes entregar pedidos que estén en estado LISTO", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenSecurityPinIsIncorrect() {
        when(orderPersistencePort.getOrderById(1L)).thenReturn(buildReadyOrder(RESTAURANT_ID, "123456"));

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.deliverOrder(1L, RESTAURANT_ID, "000000"));

        assertEquals("El pin de seguridad no es correcto", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenCancellingOrderThatDoesNotBelongToClient() {
        OrderModel order = buildPendingOrder(RESTAURANT_ID);
        order.setClientId(999L);
        when(orderPersistencePort.getOrderById(1L)).thenReturn(order);

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.cancelOrder(1L, CLIENT_ID));

        assertEquals("Este pedido no te pertenece", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }

    @Test
    void shouldThrowExceptionWhenCancellingOrderNotPendiente() {
        OrderModel order = buildOrderInPreparation(RESTAURANT_ID);
        when(orderPersistencePort.getOrderById(1L)).thenReturn(order);

        DomainException exception = assertThrows(DomainException.class,
                () -> orderUseCase.cancelOrder(1L, CLIENT_ID));

        assertEquals("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse", exception.getMessage());
        verify(orderPersistencePort, never()).saveOrder(any());
    }
}