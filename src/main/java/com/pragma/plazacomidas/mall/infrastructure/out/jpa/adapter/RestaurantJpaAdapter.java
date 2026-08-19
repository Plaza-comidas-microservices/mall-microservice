package com.pragma.plazacomidas.mall.infrastructure.out.jpa.adapter;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.pragma.plazacomidas.mall.domain.model.RestaurantModel;
import com.pragma.plazacomidas.mall.domain.spi.IRestaurantPersistencePort;
import com.pragma.plazacomidas.mall.infrastructure.exception.NoDataFoundException;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.plazacomidas.mall.infrastructure.out.jpa.repository.IRestaurantRepository;

public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    public RestaurantJpaAdapter(IRestaurantRepository restaurantRepository, IRestaurantEntityMapper restaurantEntityMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantEntityMapper = restaurantEntityMapper;
    }

    @Override
    public RestaurantModel saveRestaurant(RestaurantModel restaurantModel) {
        return restaurantEntityMapper.toRestaurantModel(
                restaurantRepository.save(restaurantEntityMapper.toEntity(restaurantModel)));
    }

    @Override
    public boolean existsById(Long restaurantId) {
        return restaurantRepository.existsById(restaurantId);
    }

    @Override
    public RestaurantModel getRestaurantById(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .map(restaurantEntityMapper::toRestaurantModel)
                .orElseThrow(NoDataFoundException::new);
    }

    @Override
    public List<RestaurantModel> getAllRestaurants(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name"));
        return restaurantEntityMapper.toRestaurantModelList(
                restaurantRepository.findAll(pageRequest).getContent());//el .getcontent descarta otros datos que trae Page, como getTotalPages.
    }
}
