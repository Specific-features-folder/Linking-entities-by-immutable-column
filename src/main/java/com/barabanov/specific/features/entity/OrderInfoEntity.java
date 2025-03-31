package com.barabanov.specific.features.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinColumnsOrFormulas;
import org.hibernate.annotations.JoinFormula;

import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true, exclude = "orderHandbookInfoEntity")
@Data
@SuperBuilder
@NoArgsConstructor
@Entity(name = "OrderInfo")
public class OrderInfoEntity extends SubsystemEntityBase {

    private String problemDesc;

    private OffsetDateTime creationDate;

    private OffsetDateTime dueDate;

    private String subsystemName;

    /**
     * Важно указать fetch как EAGER, поскольку:
     * 1. Если связка сущностей происходит не по PK другой сущности, то де-факто будет выполняться EAGER инициализация, даже если прописать LAZY.
     * Будет так из-за контракта proxy. Hibernate не знает удастся ли ему когда-то потом, при необходимости получить сущность по таким данным идёт проверять select'ом.
     * А раз уже всё равно сделал селект то для оптимизации подтянет в таком случае и данные.
     * Если hibernate отложит инициализацию сущности, а потом выяснится, что такой сущности нет, то это нарушит контракт прокси.
     * (вкратце контракт proxy такой: объект есть, просто доступ к нему вы получите позже, как понадобится. Нельзя, чтобы при обращении к реальному объекту через proxy выяснилось что объекта нет и там null)
     * Так что, чтобы выяснить нужно ли писать в поле null или прокси Hibernate придётся выполнить select этой сущности.
     *
     * Если у связываемой сущности сделать составной PK и связывать по нему, то, возможно, отложенная инициализация и будет работать.
     * Поскольку при указании связи как LAZY всегда будет отдельный подзапрос для проверки существования сущности в БД лучше будет указать связь как EAGER и всегда её доставать через join
     */
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "subsystem_name", referencedColumnName = "subsystemName")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "orderHandbookId", referencedColumnName = "id"))
    })
    @ManyToOne(fetch = FetchType.EAGER)
    private OrderHandbookInfoEntity orderHandbookInfoEntity;
}
