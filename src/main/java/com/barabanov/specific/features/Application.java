package com.barabanov.specific.features;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        TestClass testClass = context.getBean(TestClass.class);

        testClass.clearDb();
        testClass.fillDb();
        testClass.shouldGetOrderInfoListWithNonExistsServiceKeyIdToo();
    }

}
