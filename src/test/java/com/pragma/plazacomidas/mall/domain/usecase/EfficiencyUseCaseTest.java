package com.pragma.plazacomidas.mall.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.model.EfficiencyReportModel;
import com.pragma.plazacomidas.mall.domain.model.OrderModel;
import com.pragma.plazacomidas.mall.domain.model.OrderTimingModel;
import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;
import com.pragma.plazacomidas.mall.domain.spi.IOrderPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IOrderTimingPort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;

@ExtendWith(MockitoExtension.class)
class EfficiencyUseCaseTest {

    @Mock
    private IOrderPersistencePort orderPersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IOrderTimingPort orderTimingPort;

    private EfficiencyUseCase efficiencyUseCase;

    private static final Long RESTAURANT_ID = 1L;
    private static final Long OWNER_ID = 1L;

    @BeforeEach
    void setUp() {
        efficiencyUseCase = new EfficiencyUseCase(orderPersistencePort, restaurantPersistencePort, orderTimingPort);
    }

    private RestaurantModel buildRestaurant(Long ownerId) {
        return new RestaurantModel(RESTAURANT_ID, "Restaurante", "123", "Calle 1", "+573000000000", "url", ownerId);
    }

    private OrderModel buildDeliveredOrder(Long id, Long employeeId) {
        OrderModel order = new OrderModel();
        order.setId(id);
        order.setRestaurantId(RESTAURANT_ID);
        order.setStatus("ENTREGADO");
        order.setAssignedEmployeeId(employeeId);
        return order;
    }

    @Test
    void shouldReturnEfficiencyReportWithOrderDurationsAndEmployeeRanking() {
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(buildRestaurant(OWNER_ID));
        when(orderPersistencePort.getDeliveredOrdersByRestaurant(RESTAURANT_ID))
                .thenReturn(List.of(buildDeliveredOrder(1L, 5L), buildDeliveredOrder(2L, 5L)));

        Instant now = Instant.now();
        when(orderTimingPort.getOrderTimings(List.of(1L, 2L))).thenReturn(List.of(
                new OrderTimingModel(1L, now.minusSeconds(100), now),
                new OrderTimingModel(2L, now.minusSeconds(60), now)
        ));

        EfficiencyReportModel result = efficiencyUseCase.getEfficiencyReport(RESTAURANT_ID, OWNER_ID);

        assertEquals(2, result.getOrderEfficiencies().size());
        assertEquals(1, result.getEmployeeRanking().size());
        assertEquals(5L, result.getEmployeeRanking().get(0).getEmployeeId());
        assertEquals(80.0, result.getEmployeeRanking().get(0).getAverageDurationSeconds());
    }

    @Test
    void shouldReturnEmptyReportWhenNoDeliveredOrders() {
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(buildRestaurant(OWNER_ID));
        when(orderPersistencePort.getDeliveredOrdersByRestaurant(RESTAURANT_ID)).thenReturn(List.of());

        EfficiencyReportModel result = efficiencyUseCase.getEfficiencyReport(RESTAURANT_ID, OWNER_ID);

        assertEquals(0, result.getOrderEfficiencies().size());
        assertEquals(0, result.getEmployeeRanking().size());
    }

    @Test
    void shouldThrowExceptionWhenRestaurantIdIsNull() {
        DomainException exception = assertThrows(DomainException.class,
                () -> efficiencyUseCase.getEfficiencyReport(null, OWNER_ID));

        assertEquals("El id del restaurante es obligatorio", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenRestaurantDoesNotExist() {
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(false);

        DomainException exception = assertThrows(DomainException.class,
                () -> efficiencyUseCase.getEfficiencyReport(RESTAURANT_ID, OWNER_ID));

        assertEquals("El restaurante indicado no existe", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAuthenticatedUserIsNotTheOwner() {
        when(restaurantPersistencePort.existsById(RESTAURANT_ID)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurantById(RESTAURANT_ID)).thenReturn(buildRestaurant(999L));

        DomainException exception = assertThrows(DomainException.class,
                () -> efficiencyUseCase.getEfficiencyReport(RESTAURANT_ID, OWNER_ID));

        assertEquals("No eres el propietario de este restaurante", exception.getMessage());
    }
}
