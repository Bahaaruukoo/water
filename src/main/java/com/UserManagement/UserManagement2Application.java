package com.UserManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  // Enable scheduling in Spring Boot
public class UserManagement2Application {

	public static void main(String[] args) {
		SpringApplication.run(UserManagement2Application.class, args);
	}

}
