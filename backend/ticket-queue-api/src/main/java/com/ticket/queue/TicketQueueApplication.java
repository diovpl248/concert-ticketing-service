package com.ticket.queue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.ticket.core", "com.ticket.queue"})
@EntityScan(basePackages = "com.ticket.core")
@EnableJpaRepositories(basePackages = "com.ticket.core")
public class TicketQueueApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketQueueApplication.class, args);
	}

}
