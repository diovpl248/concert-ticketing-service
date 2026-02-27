package com.ticket.queue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
	exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class},
	scanBasePackages = {"com.ticket.core.common", "com.ticket.core.domain.queue", "com.ticket.core.config", "com.ticket.queue"}
)
public class TicketQueueApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketQueueApplication.class, args);
	}

}
