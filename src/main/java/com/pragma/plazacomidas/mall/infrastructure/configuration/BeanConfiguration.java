package com.pragma.plazacomidas.mall.infrastructure.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.pragma.plazacomidas.mall.domain.api.IRestaurantServicePort;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazacomidas.mall.domain.spi.IUserValidationPort;
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
    private final IUserValidationPort userValidationPort;

    @Bean
    public IRestaurantPersistencePort restaurantPersistencePort() {
        return new RestaurantJpaAdapter(restaurantRepository, restaurantEntityMapper);
    }

    @Bean
    public IRestaurantServicePort restaurantServicePort() {
        return new RestaurantUseCase(restaurantPersistencePort(), userValidationPort);
    }

}
