package com.barabanov.specific.features;

import com.barabanov.specific.features.dto.OrderSavingInfo;
import com.barabanov.specific.features.entity.OrderHandbookInfoEntity;
import com.barabanov.specific.features.entity.OrderInfoEntity;
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
    public void shouldGetOrderInfoListWithNonExistsServiceKeyIdToo() {
        Set<Long> orderInfoIdSet = StreamSupport.stream(orderInfoRepository.findAll().spliterator(), false)
                .map(OrderInfoEntity::getId)
                .collect(Collectors.toSet());
        assert orderInfoIdSet.contains(578933223673123L);
        assert orderInfoIdSet.contains(6345239879122L);
    }

    public void shouldUpdateSubsystemNameInOrderInfo() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(578933223673123L).get();
            assert orderInfoEntity.getOrderHandbookInfoEntity().getId() == 780954782343L;

            orderInfoEntity.setSubsystemName("subsystem_3");
        });

        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(578933223673123L).get();
            assert orderInfoEntity.getSubsystemName().equals("subsystem_3");
            assert orderInfoEntity.getOrderHandbookInfoEntity() == null;
        });

    }


    @Transactional
    public void getOrderInfoWithNonExistServiceKeyId() {
        OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(73947824798L).get();

        assert orderInfoEntity.getOrderHandbookInfoEntity() == null;
    }


    @Transactional
    public void shouldNotSetHandbookSubsystemNameForUpdateOrderInfo() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
                    OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(578933223673123L).get();
                    OrderHandbookInfoEntity orderHandbookInfoEntity = orderHandbookInfoRepository.findById(6345239879122L).get();
                    orderInfoEntity.setOrderHandbookInfoEntity(orderHandbookInfoEntity);
                    orderInfoRepository.save(orderInfoEntity);
                }
        );
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(578933223673123L).get();
            assert orderInfoEntity.getOrderHandbookInfoEntity().getId() == 780954782343L;
            assert orderInfoEntity.getSubsystemName().equals("subsystem_1");
        });
    }


    public void shouldNotUpdateSubsystemNameForUpdateOrderHandbookInfo() {
        transactionTemplate.executeWithoutResult(transactionStatus -> {
                    OrderHandbookInfoEntity orderHandbookInfoEntity = orderHandbookInfoRepository.findById(780954782343L).get();
                    orderHandbookInfoEntity.setSubsystemName("subsystem_3");
                    orderHandbookInfoRepository.save(orderHandbookInfoEntity);
                }
        );
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(578933223673123L).get();
            assert orderInfoEntity.getOrderHandbookInfoEntity() == null;
            assert orderInfoEntity.getSubsystemName().equals("subsystem_1");
        });
    }


    @Transactional
    public void shouldJustGetOrderHandbook() {

        OrderInfoEntity orderInfoEntity = orderInfoRepository.findById(578933223673123L).get();
        assert orderInfoEntity.getOrderHandbookInfoEntity().getId() == 1111L;
    }


    @Transactional
    public void fillDb() {
        /*
        Также, одна из задач - быстрое сохранение основной сущности, без подзапросов. Поэтому основная сущность сохраняется без участия hibernate.
        Так что помимо основной задачи (научить Hibernate использовать столбец для связи, при том не управляя этим столбцом)
        есть ещё дополнительная - проверить корректное поведения hibernate в случае если данные для связи с вторичной сущностью в основной сущности есть, а самой вторичной сущности нет.
        Нужно чтобы поле OrderHandbookInfoEntity в таком случае просто заполнялось как null, и не выбрасывалось нигде ошибок.

         */
        List<OrderSavingInfo> orderInfos = List.of(OrderSavingInfo.builder()
                        .generatedId(578933223673123L)
                        .id(12345L)
                        .problemDesc("моя админская проблема")
                        .subsystemName("subsystem_1")
                        .orderHandbookId(1111L).build(),
                OrderSavingInfo.builder()
                        .generatedId(73947824798L)
                        .id(67890L)
                        .problemDesc("моя клиентская проблема")
                        .subsystemName("subsystem_1")
                        .orderHandbookId(-9999L)
                        .build());
        jdbcTemplate.batchUpdate("""
                        INSERT INTO ${schema}.order_info(generated_id, id, problem_desc, subsystem_name, order_handbook_id)
                        VALUES (?, ?, ?, ?, ?)
                        """.replace("${schema}", schemaName),
                orderInfos, 2, (PreparedStatement ps, OrderSavingInfo orderInfo) -> {
                    ps.setLong(1, orderInfo.getGeneratedId());
                    ps.setLong(2, orderInfo.getId());
                    ps.setString(3, orderInfo.getProblemDesc());
                    ps.setString(4, orderInfo.getSubsystemName());
                    ps.setObject(5, orderInfo.getOrderHandbookId());
                }
                );

        handbookInfoRepository.save(OrderHandbookInfoEntity.builder()
                .generatedId(780954782343L)
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
                .generatedId(6345239879122L)
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
