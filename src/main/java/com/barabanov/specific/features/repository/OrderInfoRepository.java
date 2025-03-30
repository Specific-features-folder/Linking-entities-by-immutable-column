package com.barabanov.specific.features.repository;

import com.barabanov.specific.features.entity.OrderInfoEntity;
import org.springframework.data.repository.CrudRepository;

public interface OrderInfoRepository extends CrudRepository<OrderInfoEntity, Long> {

}
