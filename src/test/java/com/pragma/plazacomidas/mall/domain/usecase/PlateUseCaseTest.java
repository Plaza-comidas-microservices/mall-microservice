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
import com.pragma.plazacomidas.mall.domain.spi.IPlatePersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;

@ExtendWith(MockitoExtension.class)
class PlateUseCaseTest {

    @Mock
    private IPlatePersistencePort platePersistencePort;

    @Mock
    private IRestaurantPersistencePort restaurantPersistencePort;

    private PlateUseCase plateUseCase;

    @BeforeEach
    void setUp() {
        plateUseCase = new PlateUseCase(platePersistencePort, restaurantPersistencePort);
    }

    // Método auxiliar para crear un plato válido
    private PlateModel buildValidPlate() {
        return new PlateModel(null, "Hamburguesa doble", 25000, "Doble carne con queso",
                "http://img.com/burger.png", "Comida rápida", false, 1L);
    }

    // ---------- HAPPY PATH ----------

    @Test
    void shouldCreatePlateSuccessfullyWhenDataIsValid() {
        // Given
        PlateModel plateModel = buildValidPlate();
        PlateModel plateGuardado = new PlateModel(1L, "Hamburguesa doble", 25000, "Doble carne con queso",
                "http://img.com/burger.png", "Comida rápida", true, 1L);

        when(restaurantPersistencePort.existsById(1L)).thenReturn(true);
        when(platePersistencePort.savePlate(any(PlateModel.class))).thenReturn(plateGuardado);

        // When
        PlateModel resultado = plateUseCase.createPlate(plateModel);

        // Then
        assertEquals(1L, resultado.getId());
        assertTrue(resultado.isActive());
        verify(restaurantPersistencePort, times(1)).existsById(1L);
        verify(platePersistencePort, times(1)).savePlate(any(PlateModel.class));
    }

    @Test
    void shuldUpdatePlateSuccesfully(){
        PlateModel existingPlate = buildValidPlate();
        existingPlate.setId(1L);

        when(platePersistencePort.getPlateById(1L)).thenReturn(existingPlate);
        when(platePersistencePort.savePlate(any(PlateModel.class))).thenAnswer(invocation -> invocation.getArgument(0)); //Esto me devuelve exactamente lo que le pasé como argumento

        PlateModel plateModelUpdated = plateUseCase.updatePlate(1L, 20000, "Doble carne ANGUS con queso");

        assertEquals(1L, plateModelUpdated.getId()); //Que no cambie el ID
        assertEquals(20000, plateModelUpdated.getPrice());
        assertEquals("Doble carne ANGUS con queso", plateModelUpdated.getDescription());
    }

    // ---------- SAD PATHS ----------

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setName("  ");

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel));

        assertEquals("El nombre del plato no puede estar vacío", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenPriceIsZeroOrNegative() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setPrice(0);

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel));

        assertEquals("El precio del plato debe ser un número entero positivo mayor a 0", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenDescriptionIsBlank() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setDescription("");

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel));

        assertEquals("La descripción del plato no puede estar vacía", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenUrlIsBlank() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setUrl(null);

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel));

        assertEquals("La URL de la imagen no puede estar vacía", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenCategoryIsBlank() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setCategory(" ");

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel));

        assertEquals("La categoría no puede estar vacía", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenRestaurantIdIsNull() {
        PlateModel plateModel = buildValidPlate();
        plateModel.setRestaurantId(null);

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel));

        assertEquals("El plato debe estar asociado a un restaurante", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shouldThrowExceptionWhenRestaurantDoesNotExist() {
        PlateModel plateModel = buildValidPlate();
        when(restaurantPersistencePort.existsById(anyLong())).thenReturn(false);

        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.createPlate(plateModel));

        assertEquals("El restaurante indicado no existe", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shuldThrowExceptionWhenThePriceIsZero(){
        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.updatePlate(1L, 0, "Doble carne ANGUS con queso"));

        assertEquals("El precio del plato debe ser entero y positivo", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

    @Test
    void shuldThrowExceptionWhenThedescriptionIsBlank(){
        DomainException exception = assertThrows(DomainException.class,
                () -> plateUseCase.updatePlate(1L, 10000, ""));

        assertEquals("La descripción no puede estar vacía", exception.getMessage());
        verify(platePersistencePort, never()).savePlate(any());
    }

}
