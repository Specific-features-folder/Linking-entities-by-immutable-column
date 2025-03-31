package com.barabanov.specific.features.entity;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@EqualsAndHashCode(callSuper=false)
@SuperBuilder
@NoArgsConstructor
@Entity(name = "OrderHandbookInfo")
public class OrderHandbookInfoEntity extends SubsystemEntityBase {

    private String code;

    private String subCode;

    private String marker;

    private String description;

    private String orderType;

    private String dueDatePolicy;
}
