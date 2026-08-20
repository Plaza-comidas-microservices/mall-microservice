package com.pragma.plazacomidas.mall.domain.usecase;

import java.util.List;

import com.pragma.plazacomidas.mall.domain.api.IOrderServicePort;
import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.model.OrderModel;
import com.pragma.plazacomidas.mall.domain.model.PlateModel;
import com.pragma.plazacomidas.mall.domain.spi.IOrderPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IPlatePersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazacomidas.mall.domain.model.OrderItemModel;

public class OrderUseCase implements IOrderServicePort{

    private final IOrderPersistencePort orderPersistencePort;
    private final IPlatePersistencePort platePersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;

    public OrderUseCase (IOrderPersistencePort orderPersistencePort, IPlatePersistencePort platePersistencePort, IRestaurantPersistencePort restaurantPersistencePort){
        this.orderPersistencePort = orderPersistencePort;
        this.platePersistencePort = platePersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
    }

    @Override
    public OrderModel createOrder(OrderModel orderModel, Long authenticatedClientId) {
        Long restaurantId = orderModel.getRestaurantId();

        if ( restaurantId == null){
            throw new DomainException("El restaurante no existe");
        }else if (!restaurantPersistencePort.existsById(restaurantId)){
            throw new DomainException("El restaurante no existe");
        }else if (orderModel.getItems() == null || orderModel.getItems().isEmpty()){
            throw new DomainException("No hay pedidos");
        }else if (orderPersistencePort.hasActiveOrder(authenticatedClientId)) {
            throw new DomainException("Ya tienes un pedido en curso, no puedes realizar otro");
        }   

        for (OrderItemModel item : orderModel.getItems() ){
            PlateModel plate = platePersistencePort.getPlateById(item.getPlateId());
            if(item.getQuantity() <= 0 ){
               throw new DomainException("No hay unidades disponibles");
            }else if(!(plate.getRestaurantId().equals(restaurantId))){
                throw new DomainException("El plato no pertenece a este restaurante");
            }else if(plate.isActive() == false){
                throw new DomainException("Este plato está actualmente fuera del menú");
            }
            
        }

        orderModel.setClientId(authenticatedClientId); 
        orderModel.setStatus("PENDIENTE");

        return orderPersistencePort.saveOrder(orderModel);

    }

    @Override
    public List<OrderModel> getOrdersByRestaurantAndStatus(Long restaurantId, String status, int page, int size) {
        if (restaurantId == null) {
            throw new DomainException("No perteneces a ningún restaurante");
        } else if (status == null || status.isBlank()) {
            throw new DomainException("Debes indicar el estado por el cual filtrar");
        } else if (page < 0) {
            throw new DomainException("El número de página no puede ser negativo");
        } else if (size <= 0) {
            throw new DomainException("El tamaño de página debe ser mayor a 0");
        }
        return orderPersistencePort.getOrdersByRestaurantAndStatus(restaurantId, status, page, size);
    }

    @Override
    public OrderModel assignOrder(Long orderId, Long employeeId, Long employeeRestaurantId) {
        OrderModel order = orderPersistencePort.getOrderById(orderId);

        if (!order.getRestaurantId().equals(employeeRestaurantId)) {
            throw new DomainException("Este pedido no pertenece a tu restaurante");
        } else if (!"PENDIENTE".equals(order.getStatus())) {
            throw new DomainException("Solo puedes asignarte pedidos que estén en estado PENDIENTE");
        }

        order.setAssignedEmployeeId(employeeId);
        order.setStatus("EN_PREPARACION");

        return orderPersistencePort.saveOrder(order);
    }

}
