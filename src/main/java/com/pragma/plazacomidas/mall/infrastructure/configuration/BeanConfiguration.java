package com.pragma.plazacomidas.mall.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pragma.plazacomidas.mall.domain.api.IOrderServicePort;
import com.pragma.plazacomidas.mall.domain.api.IPlateServicePort;
import com.pragma.plazacomidas.mall.domain.api.IRestaurantServicePort;
import com.pragma.plazacomidas.mall.domain.spi.IClientContactPort;
import com.pragma.plazacomidas.mall.domain.spi.INotificationPort;
import com.pragma.plazacomidas.mall.domain.spi.IOrderPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IPlatePersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IUserValidationPort;
import com.pragma.plazacomidas.mall.domain.usecase.OrderUseCase;
import com.pragma.plazacomidas.mall.domain.usecase.PlateUseCase;
import com.pragma.plazacomidas.mall.domain.usecase.RestaurantUseCase;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.adapter.OrderJpaAdapter;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.adapter.PlateJpaAdapter;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.mapper.IOrderEntityMapper;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.mapper.IPlateEntityMapper;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.repository.IOrderRepository;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.repository.IPlateRepository;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.repository.IRestaurantRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IUserValidationPort userValidationPort;
    private final IClientContactPort clientContactPort;
    private final INotificationPort notificationPort;
    private final IPlateRepository plateRepository;
    private final IPlateEntityMapper plateEntityMapper;
    private final IOrderRepository orderRepository;
    private final IOrderEntityMapper orderEntityMapper;

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort() {
        return new RestaurantUseCase(restaurantPersistencePort(), userValidationPort);
    }

    @Bean
    public IPlatePersistencePort platePersistencePort() {
        return new PlateJpaAdapter(plateRepository, plateEntityMapper);
    }

    @Bean
    public IPlateServicePort plateServicePort() {
        return new PlateUseCase(platePersistencePort(), restaurantPersistencePort());
    }

    @Bean
    public IOrderPersistencePort orderPersistencePort() {
        return new OrderJpaAdapter(orderRepository, orderEntityMapper);
    }

    @Bean
    public IOrderServicePort orderServicePort() {
        return new OrderUseCase(orderPersistencePort(), platePersistencePort(), restaurantPersistencePort(),
            clientContactPort, notificationPort);
    }

}
