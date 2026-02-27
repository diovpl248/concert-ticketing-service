package com.ticket.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.ticket.core", "com.ticket.app"})
@EntityScan(basePackages = "com.ticket.core")
@EnableJpaRepositories(basePackages = "com.ticket.core")
public class TicketAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketAppApplication.class, args);
    }

}
