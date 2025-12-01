package com.project.qr_order_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 스케줄러 기능 켜기
@SpringBootApplication
public class QrOrderSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrOrderSystemApplication.class, args);
	}

}
