package com.pragma.plazacomidas.mall.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pragma.plazacomidas.mall.domain.api.IRestaurantServicePort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazacomidas.mall.domain.usecase.RestaurantUseCase;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.adapter.RestaurantJpaAdapter;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.repository.IRestaurantRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort() {
        return new RestaurantUseCase(restaurantPersistencePort());
    }
}
