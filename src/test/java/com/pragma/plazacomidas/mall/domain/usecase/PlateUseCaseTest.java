package com.pragma.plazacomidas.mall.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.pragma.plazacomidas.mall.domain.model.PlateModel;
import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;
import com.pragma.plazacomidas.mall.domain.spi.IPlatePersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;

@ExtendWith(MockitoExtension.class)
class PlateUseCaseTest {

    @Mock
    private IPlatePersistencePort platePersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    private PlateUseCase plateUseCase;

    private static final Long OWNER_ID = 1L;

    @BeforeEach
    void setUp() {
        plateUseCase = new PlateUseCase(platePersistencePort, restaurantPersistencePort);
    }

    private PlateModel buildValidPlate() {
        return new PlateModel(null, "Hamburguesa doble", 25000, "Doble carne con queso",
                "http://img.com/burger.png", "Comida rápida", false, 1L);
    }

    private RestaurantModel buildRestaurantOwnedBy(Long ownerId) {
        RestaurantModel restaurant = new RestaurantModel();
        restaurant.setId(1L);
        restaurant.setOwnerId(ownerId);
        return restaurant;
    }

    // ---------- HAPPY PATH ----------

    @Test
    void shouldCreatePlateSuccessfullyWhenDataIsValid() {
        PlateModel plateModel = buildValidPlate();
        PlateModel plateGuardado = new PlateModel(1L, "Hamburguesa doble", 25000, "Doble carne con queso",
                "http://img.com/burger.png", "Comida rápida", true, 1L);

        when(restaurantPersistencePort.existsById(1L)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurantById(1L)).thenReturn(buildRestaurantOwnedBy(OWNER_ID));
        when(platePersistencePort.savePlate(any(PlateModel.class))).thenReturn(plateGuardado);

        PlateModel resultado = plateUseCase.createPlate(plateModel, OWNER_ID);

        assertEquals(1L, resultado.getId());
        assertTrue(resultado.isActive());
        verify(restaurantPersistencePort, times(1)).existsById(1L);
        verify(platePersistencePort, times(1)).savePlate(any(PlateModel.class));
    }

    @Test
    void shuldUpdatePlateSuccesfully() {
        PlateModel existingPlate = buildValidPlate();
        existingPlate.setId(1L);

        when(platePersistencePort.getPlateById(1L)).thenReturn(existingPlate);
        when(restaurantPersistencePort.getRestaurantById(1L)).thenReturn(buildRestaurantOwnedBy(OWNER_ID));
        when(platePersistencePort.savePlate(any(PlateModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlateModel plateModelUpdated = plateUseCase.updatePlate(1L, 20000, "Doble carne ANGUS con queso", OWNER_ID);

        assertEquals(1L, plateModelUpdated.getId());
        assertEquals(20000, plateModelUpdated.getPrice());
        assertEquals("Doble carne ANGUS con queso", plateModelUpdated.getDescription());
    }

    @Test
    void shouldToggleStatusSuccessfullyWhenAuthenticatedUserIsTheOwner() {
        PlateModel existingPlate = buildValidPlate();
        existingPlate.setId(1L);
        existingPlate.setActive(true);

        when(platePersistencePort.getPlateById(1L)).thenReturn(existingPlate);
        when(restaurantPersistencePort.getRestaurantById(1L)).thenReturn(buildRestaurantOwnedBy(OWNER_ID));
        when(platePersistencePort.savePlate(any(PlateModel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PlateModel result = plateUseCase.toggPlateStatus(1L, false, OWNER_ID);

        assertEquals(1L, result.getId());
        assertEquals(false, result.isActive());
        verify(platePersistencePort, times(1)).savePlate(any(PlateModel.class));
    }

    // ---------- SAD PATHS ----------

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setName("  ");

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel, OWNER_ID));

        assertEquals("El nombre del plato no puede estar vacío", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsZeroOrNegative() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setPrice(0);

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel, OWNER_ID));

        assertEquals("El precio del plato debe ser un número entero positivo mayor a 0", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsBlank() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setDescription("");

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel, OWNER_ID));

        assertEquals("La descripción del plato no puede estar vacía", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenUrlIsBlank() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setUrl(null);

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel, OWNER_ID));

        assertEquals("La URL de la imagen no puede estar vacía", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsBlank() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setCategory(" ");

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel, OWNER_ID));

        assertEquals("La categoría no puede estar vacía", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenRestaurantIdIsNull() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setRestaurantId(null);

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel, OWNER_ID));

        assertEquals("El plato debe estar asociado a un restaurante", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenRestaurantDoesNotExist() {
        PlateModel plateModel = buildValidPlate();
        when(restaurantPersistencePort.existsById(anyLong())).thenReturn(false);

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel, OWNER_ID));

        assertEquals("El restaurante indicado no existe", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenAuthenticatedUserIsNotTheRestaurantOwner() {
        PlateModel plateModel = buildValidPlate();

        when(restaurantPersistencePort.existsById(1L)).thenReturn(true);
        when(restaurantPersistencePort.getRestaurantById(1L)).thenReturn(buildRestaurantOwnedBy(OWNER_ID));

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel, 999L));

        assertEquals("No eres el propietario de este restaurante", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shuldThrowExceptionWhenThePriceIsZero() {
        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.updatePlate(1L, 0, "Doble carne ANGUS con queso", OWNER_ID));

        assertEquals("El precio del plato debe ser entero y positivo", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shuldThrowExceptionWhenThedescriptionIsBlank() {
        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.updatePlate(1L, 10000, "", OWNER_ID));

        assertEquals("La descripción no puede estar vacía", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenTogglingStatusAndAuthenticatedUserIsNotTheOwner() {
        PlateModel existingPlate = buildValidPlate();
        existingPlate.setId(1L);

        when(platePersistencePort.getPlateById(1L)).thenReturn(existingPlate);
        when(restaurantPersistencePort.getRestaurantById(1L)).thenReturn(buildRestaurantOwnedBy(OWNER_ID));

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.toggPlateStatus(1L, false, 999L));

        assertEquals("No eres el propietario de este restaurante", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }
}
