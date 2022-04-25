package com.edu.moneywayapi.webApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories("com.edu.moneywayapi")
@EntityScan("com.edu.moneywayapi")
@ComponentScan("com.edu.moneywayapi")
@SpringBootApplication
public class MoneyWayApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyWayApiApplication.class, args);
    }

}
