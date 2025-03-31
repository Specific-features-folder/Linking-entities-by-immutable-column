package com.barabanov.specific.features.repository;

import com.barabanov.specific.features.entity.OrderInfoEntity;
import com.barabanov.specific.features.entity.SubsystemEntityId;
import org.springframework.data.repository.CrudRepository;

public interface OrderInfoRepository extends CrudRepository<OrderInfoEntity, SubsystemEntityId> {

}
