package com.pragma.plazacomidas.mall.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IUserValidationPort;

@ExtendWith(MockitoExtension.class)
class RestaurantUseCaseTest {

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    @Mock
    private IUserValidationPort userValidationPort;

    private RestaurantUseCase restaurantUseCase;

    @BeforeEach
    void setUp() {
        restaurantUseCase = new RestaurantUseCase(restaurantPersistencePort, userValidationPort);
    }

    private RestaurantModel buildValidRestaurant() {
        return new RestaurantModel(null, "Mall Burger", "123456789", "Calle 10 # 20-30",
                "+573005698325", "http://logo.com/mall.png", 1L);
    }

    // ---------- HAPPY PATH ----------

    @Test
    void shouldCreateRestaurantSuccessfullyWhenDataIsValid() {
        // Given
        RestaurantModel restaurantModel = buildValidRestaurant();
        RestaurantModel restaurantGuardado = new RestaurantModel(1L, "Mall Burger", "123456789",
                "Calle 10 # 20-30", "+573005698325", "http://logo.com/mall.png", 1L);

        when(userValidationPort.isOwner(1L)).thenReturn(true);
        when(restaurantPersistencePort.saveRestaurant(any(RestaurantModel.class))).thenReturn(restaurantGuardado);

        // When
        RestaurantModel resultado = restaurantUseCase.createRestaurant(restaurantModel);

        // Then
        assertEquals(1L, resultado.getId());
        assertEquals("Mall Burger", resultado.getName());
        verify(userValidationPort, times(1)).isOwner(1L);
        verify(restaurantPersistencePort, times(1)).saveRestaurant(any(RestaurantModel.class));
    }

    // ---------- SAD PATHS ----------

    @Test
    void shouldThrowExceptionWhenNameContainsOnlyNumbers() {
        // Given
        RestaurantModel restaurantModel = buildValidRestaurant();
        restaurantModel.setName("12345");

        // When + Then
        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.createRestaurant(restaurantModel));

        assertEquals("El nombre del restaurante es obligatorio y no puede contener solo números",
                exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void shouldThrowExceptionWhenNitIsNotNumeric() {
        // Given
        RestaurantModel restaurantModel = buildValidRestaurant();
        restaurantModel.setNit("NIT-123");

        // When + Then
        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.createRestaurant(restaurantModel));

        assertEquals("El NIT debe ser únicamente numérico", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void shouldThrowExceptionWhenPhoneIsInvalid() {
        // Given
        RestaurantModel restaurantModel = buildValidRestaurant();
        restaurantModel.setPhone("abc123");

        // When + Then
        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.createRestaurant(restaurantModel));

        assertEquals("El teléfono no es válido. Ejemplo +573005698325", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }

    @Test
    void shouldThrowExceptionWhenOwnerIsNotValid() {
        // Given
        RestaurantModel restaurantModel = buildValidRestaurant();
        when(userValidationPort.isOwner(anyLong())).thenReturn(false);

        // When + Then
        DomainException exception = assertThrows(DomainException.class,
                () -> restaurantUseCase.createRestaurant(restaurantModel));

        assertEquals("El id del propietario no es válido", exception.getMessage());
        verify(restaurantPersistencePort, never()).saveRestaurant(any());
    }
}
