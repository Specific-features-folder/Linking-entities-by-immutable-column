package com.barabanov.specific.features.repository;

import com.barabanov.specific.features.entity.OrderHandbookInfoEntity;
import com.barabanov.specific.features.entity.SubsystemEntityId;
import org.springframework.data.repository.CrudRepository;

public interface OrderHandbookInfoRepository extends CrudRepository<OrderHandbookInfoEntity, SubsystemEntityId> {
}
