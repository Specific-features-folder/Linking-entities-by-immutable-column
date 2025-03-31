package com.barabanov.specific.features.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;


@Builder
@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OrderSavingInfo {
    Long id;
    String problemDesc;
    OffsetDateTime creationDate;
    OffsetDateTime dueDate;
    String subsystemName;
    Long orderHandbookId;
}
