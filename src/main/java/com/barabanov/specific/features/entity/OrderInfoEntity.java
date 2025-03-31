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

    /**
     * Важно указать fetch как EAGER, поскольку:
     * 1. Если связка сущностей происходит НЕ по PK другой сущности, то де-факто будет выполняться EAGER инициализация, даже если прописать LAZY.
     * Будет так из-за контракта proxy. Hibernate не знает удастся ли ему когда-то потом, при необходимости получить сущность по таким данным и идёт проверять select'ом.
     * А раз уже всё равно сделал select, то для оптимизации подтянет в таком случае и данные.
     * Если hibernate отложит инициализацию сущности, а потом выяснится, что такой сущности нет, то это нарушит контракт прокси.
     * (вкратце контракт proxy такой: объект есть, просто доступ к нему вы получите позже, как понадобится. Нельзя, чтобы при обращении к реальному объекту через proxy выяснилось что объекта нет и там null)
     * Так что, чтобы выяснить нужно ли писать в поле null или прокси Hibernate придётся выполнить select этой сущности.
     *
     * 2.Если связка будет по PK другой сущности, то использование LAZY работать будет, но его поведение опасно, в таких условиях сохранения / получения сущностей.
     * У Hibernate будут данные первичного ключа и он считает что это foreign key и данных не может не быть, так что проинициализировать их можно и потом.
     * Но когда вызовется отложенная инициализация объекта, которого на самом деле нет в БД вы получите даже не null, а EntityNotFoundfException.
     * Такое поведение обусловлено нарушением контракта proxy.
     *
     * Если же указать fetch как EAGER, то hibernate ещё при запросе OperationInfoEntity увидит что данных для ServiceKeyInfoEntity нет,
     * не будет создавать никакого proxy и вставит null.
     * Чтобы поведение было прозрачным в обоих вариантах и не вызывало проблем, лучше поставить fetch EAGER
     */
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "subsystem_name", referencedColumnName = "subsystemName")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "orderHandbookId", referencedColumnName = "id"))
    })
    @ManyToOne(fetch = FetchType.EAGER)
    private OrderHandbookInfoEntity orderHandbookInfoEntity;
}
