package com.pragma.plazacomidas.mall.domain.usecase;

import java.security.SecureRandom;
import java.util.List;

import com.pragma.plazacomidas.mall.domain.api.IOrderServicePort;
import com.pragma.plazacomidas.mall.domain.exception.DomainException;
import com.pragma.plazacomidas.mall.domain.model.OrderModel;
import com.pragma.plazacomidas.mall.domain.model.PlateModel;
import com.pragma.plazacomidas.mall.domain.spi.IClientContactPort;
import com.pragma.plazacomidas.mall.domain.spi.INotificationPort;
import com.pragma.plazacomidas.mall.domain.spi.IOrderPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IPlatePersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazacomidas.mall.domain.model.OrderItemModel;

public class OrderUseCase implements IOrderServicePort{

    private static final String SECURITY_PIN_DIGITS = "0123456789";
    private static final int SECURITY_PIN_LENGTH = 6;

    private final IOrderPersistencePort orderPersistencePort;
    private final IPlatePersistencePort platePersistencePort;
    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IClientContactPort clientContactPort;
    private final INotificationPort notificationPort;
    private final SecureRandom secureRandom = new SecureRandom();

    public OrderUseCase (IOrderPersistencePort orderPersistencePort, IPlatePersistencePort platePersistencePort,
        IRestaurantPersistencePort restaurantPersistencePort, IClientContactPort clientContactPort,
        INotificationPort notificationPort){
        this.orderPersistencePort = orderPersistencePort;
        this.platePersistencePort = platePersistencePort;
        this.restaurantPersistencePort = restaurantPersistencePort;
        this.clientContactPort = clientContactPort;
        this.notificationPort = notificationPort;
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

    @Override
    public OrderModel markOrderAsReady(Long orderId, Long employeeId, Long employeeRestaurantId) {
        OrderModel order = orderPersistencePort.getOrderById(orderId);

        if (!order.getRestaurantId().equals(employeeRestaurantId)) {
            throw new DomainException("Este pedido no pertenece a tu restaurante");
        } else if (!"EN_PREPARACION".equals(order.getStatus())) {
            throw new DomainException("Solo puedes marcar como listo un pedido que esté en preparación");
        }

        String securityPin = generateSecurityPin();
        String clientPhone = clientContactPort.getClientPhone(order.getClientId());
        notificationPort.sendOrderReadySms(clientPhone, securityPin);

        order.setStatus("LISTO");
        order.setSecurityPin(securityPin);

        return orderPersistencePort.saveOrder(order);
    }

    @Override
    public OrderModel deliverOrder(Long orderId, Long employeeRestaurantId, String securityPin) {
        OrderModel order = orderPersistencePort.getOrderById(orderId);

        if (!order.getRestaurantId().equals(employeeRestaurantId)) {
            throw new DomainException("Este pedido no pertenece a tu restaurante");
        } else if (!"LISTO".equals(order.getStatus())) {
            throw new DomainException("Solo puedes entregar pedidos que estén en estado LISTO");
        } else if (securityPin == null || !securityPin.equals(order.getSecurityPin())) {
            throw new DomainException("El pin de seguridad no es correcto");
        }

        order.setStatus("ENTREGADO");

        return orderPersistencePort.saveOrder(order);
    }

    @Override
    public OrderModel cancelOrder(Long orderId, Long authenticatedClientId) {
        OrderModel order = orderPersistencePort.getOrderById(orderId);

        if (!order.getClientId().equals(authenticatedClientId)) {
            throw new DomainException("Este pedido no te pertenece");
        } else if (!"PENDIENTE".equals(order.getStatus())) {
            throw new DomainException("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse");
        }

        order.setStatus("CANCELADO");

        return orderPersistencePort.saveOrder(order);
    }

    private String generateSecurityPin() {
        StringBuilder pin = new StringBuilder(SECURITY_PIN_LENGTH);
        for (int i = 0; i < SECURITY_PIN_LENGTH; i++) {
            pin.append(SECURITY_PIN_DIGITS.charAt(secureRandom.nextInt(SECURITY_PIN_DIGITS.length())));
        }
        return pin.toString();
    }

}
