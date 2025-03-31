package com.barabanov.specific.features.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SubsystemEntityId {
    private Long id;
    private String subsystemName;
}
