package com.barabanov.specific.features;

import com.barabanov.specific.features.dto.OrderSavingInfo;
import com.barabanov.specific.features.entity.OrderHandbookInfoEntity;
import com.barabanov.specific.features.entity.OrderInfoEntity;
import com.barabanov.specific.features.entity.SubsystemEntityId;
import com.barabanov.specific.features.repository.OrderHandbookInfoRepository;
import com.barabanov.specific.features.repository.OrderInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


// Всё же класс для тестов в локальном проекте, не за чем красоту разводить с инкапсуляцией и SRP. Всё в одном месте для удобства)

@SuppressWarnings("OptionalGetWithoutIsPresent")
@RequiredArgsConstructor
@Component
public class TestClass {

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private final String schemaName;

    private final JdbcTemplate jdbcTemplate;
    private final OrderHandbookInfoRepository handbookInfoRepository;
    private final OrderInfoRepository orderInfoRepository;
    private final TransactionTemplate transactionTemplate;
    private final OrderHandbookInfoRepository orderHandbookInfoRepository;


    @Transactional
    public void shouldGetOrderHandbookWithNonExistsServiceKeyId() {
        /**
         * В случае LAZY инициализации при связи по PK без foreign key в БД возможна очень неприятная ситуация.
         * Отложенная инициализация то работать будет, но лучше бы не работала...
         * Сущность сразу не была запрошена и в поле orderHandbookInfoEntity прокси, а не null.
         * Прокси утверждает что объект есть, а его на деле не оказалось. Так что в случае LAZY без foreignKey в БД придётся каждое обращение к полю делать в try/catch и даже Optional не спасёт.
         * Ведь при запросе getOrderHandbookInfoEntity().getId() будет даже не null, а EntityNotFound, ведь это контракта нарушение прокси.
         * Это очень неудобно, поэтому не стоит использовать LAZY в таком случае. А сразу инициализировать объект или проставлять null, используя EAGER
         */
        OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(SubsystemEntityId.builder()
                .id(67890L)
                .subsystemName("subsystem_1")
                .build()).get();
        System.out.println(orderInfoEntity.getOrderHandbookInfoEntity() == null);
        System.out.println(Optional.ofNullable(orderInfoEntity.getOrderHandbookInfoEntity())
                .map(OrderHandbookInfoEntity::getId)
                .isEmpty());
    }

    @Transactional
    public void shouldGetOrderInfoListWithNonExistsServiceKeyIdToo() {
        Set<Long> orderInfoIdSet = StreamSupport.stream(orderInfoRepository.findAll().spliterator(), false)
                .map(OrderInfoEntity::getId)
                .collect(Collectors.toSet());
        System.out.println(orderInfoIdSet.contains(12345L));
        System.out.println(orderInfoIdSet.contains(67890L));
    }

    /**
     * Это некорректный тест в таком варианте, поскольку нельзя менять PK у сущности, можно сохранить новую с другим PK, но тогда тест будет идентичен 'shouldGetOrderHandbookWithNonExistsServiceKeyId'
     */
//    public void shouldUpdateSubsystemNameInOrderInfo() {
//        transactionTemplate.executeWithoutResult(transactionStatus -> {
//            OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(SubsystemEntityId.builder()
//                    .id(12345L)
//                    .subsystemName("subsystem_1")
//                    .build()).get();
//            System.out.println(orderInfoEntity.getOrderHandbookInfoEntity().getId() == 1111L);
//
//            orderInfoEntity.setSubsystemName("subsystem_3");
//            orderInfoRepository.save(orderInfoEntity);
//        });
//
//        transactionTemplate.executeWithoutResult(transactionStatus -> {
//            OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(SubsystemEntityId.builder()
//                    .id(12345L)
//                    .subsystemName("subsystem_1")
//                    .build()).get();
//            System.out.println(orderInfoEntity.getSubsystemName().equals("subsystem_3"));
//            System.out.println(orderInfoEntity.getOrderHandbookInfoEntity() == null);
//        });
//
//    }


    @Transactional
    public void getOrderInfoWithNonExistServiceKeyId() {
        OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(SubsystemEntityId.builder()
                .id(67890L)
                .subsystemName("subsystem_1")
                .build()).get();

        System.out.println(orderInfoEntity.getOrderHandbookInfoEntity() == null);
    }


    public void shouldNotSetHandbookSubsystemNameForUpdateOrderInfo() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
                    OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(SubsystemEntityId.builder()
                            .id(67890L)
                            .subsystemName("subsystem_1")
                            .build()).get();
                    OrderHandbookInfoEntity orderHandbookInfoEntity = orderHandbookInfoRepository.findById(SubsystemEntityId.builder()
                            .id(222L)
                            .subsystemName("subsystem_2")
                            .build()).get();
                    orderInfoEntity.setOrderHandbookInfoEntity(orderHandbookInfoEntity);
                    orderInfoRepository.save(orderInfoEntity);
                }
        );
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(SubsystemEntityId.builder()
                    .id(67890L)
                    .subsystemName("subsystem_1")
                    .build()).get();
            System.out.println(orderInfoEntity.getOrderHandbookInfoEntity() == null);
            System.out.println(orderInfoEntity.getSubsystemName().equals("subsystem_1"));
        });
    }


    /**
     * Это некорректный тест в таком варианте, поскольку нельзя менять PK у сущности, можно сохранить новую с другим PK, но тогда тест будет идентичен 'shouldNotSetHandbookSubsystemNameForUpdateOrderInfo'
     */
//    public void shouldNotUpdateSubsystemNameForUpdateOrderHandbookInfo() {
//        transactionTemplate.executeWithoutResult(transactionStatus -> {
//                    OrderHandbookInfoEntity orderHandbookInfoEntity = orderHandbookInfoRepository.findById(780954782343L).get();
//                    orderHandbookInfoEntity.setSubsystemName("subsystem_3");
//                    orderHandbookInfoRepository.save(orderHandbookInfoEntity);
//                }
//        );
//        transactionTemplate.executeWithoutResult(transactionStatus -> {
//            OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(578933223673123L).get();
//            System.out.println(orderInfoEntity.getOrderHandbookInfoEntity() == null);
//            System.out.println(orderInfoEntity.getSubsystemName().equals("subsystem_1"));
//        });
//    }

    @Transactional
    public void shouldJustGetOrderHandbook() {
        OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(SubsystemEntityId.builder()
                .id(12345L)
                .subsystemName("subsystem_1")
                .build()).get();
        System.out.println(orderInfoEntity.getOrderHandbookInfoEntity().getId() == 1111L);
    }


    @Transactional
    public void fillDb() {
        /**
         * Также, одна из задач - быстрое сохранение основной сущности, без подзапросов. Поэтому основная сущность сохраняется без участия hibernate.
         * Так что помимо основной задачи (научить Hibernate использовать столбец для связи, при том не управляя этим столбцом)
         * есть ещё дополнительная - проверить корректное поведения hibernate в случае если данные для связи с вторичной сущностью в основной сущности есть, а самой вторичной сущности нет.
         * Нужно чтобы поле OrderHandbookInfoEntity в таком случае просто заполнялось как null, и не выбрасывалось нигде ошибок.
         */
        List<OrderSavingInfo> orderInfos = List.of(OrderSavingInfo.builder()
                        .id(12345L)
                        .problemDesc("моя админская проблема")
                        .subsystemName("subsystem_1")
                        .orderHandbookId(1111L).build(),
                OrderSavingInfo.builder()
                        .id(67890L)
                        .problemDesc("моя клиентская проблема")
                        .subsystemName("subsystem_1")
                        .orderHandbookId(-9999L)
                        .build());
        jdbcTemplate.batchUpdate("""
                        INSERT INTO ${schema}.order_info(id, problem_desc, subsystem_name, order_handbook_id)
                        VALUES (?, ?, ?, ?)
                        """.replace("${schema}", schemaName),
                orderInfos, 2, (PreparedStatement ps, OrderSavingInfo orderInfo) -> {
                    ps.setLong(1, orderInfo.getId());
                    ps.setString(2, orderInfo.getProblemDesc());
                    ps.setString(3, orderInfo.getSubsystemName());
                    ps.setObject(4, orderInfo.getOrderHandbookId());
                }
        );

        handbookInfoRepository.save(OrderHandbookInfoEntity.builder()
                .id(1111L)
                .subsystemName("subsystem_1")
                .description("Описания для типа заявки №463872381")
                .dueDatePolicy("ND2PM")
                .marker("urgently")
                .orderType("admin_order")
                .code("1892")
                .subCode("1")
                .build());
        handbookInfoRepository.save(OrderHandbookInfoEntity.builder()
                .id(222L)
                .subsystemName("subsystem_2")
                .description("Описания для типа заявки №999437")
                .dueDatePolicy("ND2PM")
                .marker("urgently")
                .orderType("admin_order")
                .code("1891")
                .subCode("0")
                .build());
    }


    @Transactional
    public void clearDb() {
        handbookInfoRepository.deleteAll();
        orderInfoRepository.deleteAll();
    }
}
