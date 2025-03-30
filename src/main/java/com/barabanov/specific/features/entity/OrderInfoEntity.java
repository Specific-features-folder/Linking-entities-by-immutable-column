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

    /*
        Использовать LAZY, если колонки по которым связываются сущности не являются PK - бесполезно, я думаю.
        Hibernate не знает, сможет ли он по таким данным когда-то потом достать сущность т.к. это не её PK.
        И если отложит инициализацию сущности, а потом выяснится, что такой сущности нет, то это нарушит контракт прокси.
        Так что, чтобы выяснить нужно ли туда писать null или прокси Hibernate придётся выполнить select этой сущности, а если уж он всё равно выполняет select для неё, то Hibernate её и проинициализирует.
        Если у связываемой сущности сделать составной PK и связывать по нему, то, возможно, отложенная инициализация и будет работать.

        И, поскольку, если указать сущность как LAZY она всегда будет отдельный подзапрос для её получения - лучше указать связь как EAGER и всегда её доставать через join
     */
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "subsystem_name", referencedColumnName = "subsystemName")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "orderHandbookId", referencedColumnName = "id"))
    })
    @ManyToOne(fetch = FetchType.EAGER)
    private OrderHandbookInfoEntity orderHandbookInfoEntity;
}
