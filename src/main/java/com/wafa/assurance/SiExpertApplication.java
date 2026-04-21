package com.wafa.assurance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SiExpertApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiExpertApplication.class, args);
	}
}